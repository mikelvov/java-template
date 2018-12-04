package edu.spbu.matrix;
import java.awt.*;
import java.util.HashMap;
import java.io.File;
import java.util.Scanner;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Плотная матрица
 */
class Invest implements Runnable
{
  private int str1, str2;                                           //с какой строки умножать и до какой
  private DenseMatrix a, b, res;                                    // 3 матрицы
  Invest(int k, int l, DenseMatrix i, DenseMatrix j, DenseMatrix c){
    res = c;
    str1 = k;
    str2 = l;
    a = i;
    b = j;
  }
  @Override
  public void run() {                       //то что должен делать поток : делим первую матрицу на строки, умножаем на все столбцы второй и слепляем
    for (int i = str1; i < str1 + str2; i++){         //со строки стр1 до стр2
      if (i < a.r)                                    //если не вылезли за границы матрицы
      {
        for (int j = 0; j < res.c; j++) {
          for (int k = 0; k < a.c; k++) {
            res.MainMatrix[i][j] += (a.MainMatrix[i][k] * b.MainMatrix[j][k]);
          }
        }
      }
    }
  }
}



public class DenseMatrix implements Matrix {
  public int r = 0;
  public int c = 0;
  public double[][] MainMatrix;


  public DenseMatrix(String fileName)   //матрица по файлу
  {
    try {
      File f = new File(fileName);
      Scanner input = new Scanner(f);
      String[] line;
      ArrayList<Double[]> a = new ArrayList<Double[]>();             //создали расширяемый списочный массив
      Double[] tmp = {};                                      //создали пустой массив tmp // tmp - список
      int k;
      if (input.hasNextLine()) {
        line = input.nextLine().split(" ");
        k = line.length;
        tmp = new Double[line.length];                        // tmp - массив размеры длины line
        for (int i = 0; i < tmp.length; i++) {                // в цикле от 0 до размера tmp записываем итый символ line в tmp
          tmp[i] = Double.parseDouble(line[i]);               // преобразует тип String к Double
        }
        a.add(tmp);
      }
      else return;

      while (input.hasNextLine()) {                           // пока есть строки в файл
        //на каждом шаге есть очередная строка (line), которую преобразуем к массиву из чисел (tmp), а потом
        // зная их итоговые размеры собираеи всё в двумерный массив (rezult), который приравнивается к итоговому значению матрицы
        line = input.nextLine().split(" ");             // считали из файла строку с пробелом в line
        if (line.length == k) {
          tmp = new Double[line.length];                        // tmp - массив размеры длины line
          for (int i = 0; i < tmp.length; i++) {                // в цикле от 0 до размера tmp записываем итый символ line в tmp
            tmp[i] = Double.parseDouble(line[i]);               // преобразует тип String к Double
          }
          a.add(tmp);                                           //добавляем tmp к расширяемому списочному массиву
        }
        else return;
      }
      double[][] result = new double[a.size()][tmp.length];   // результат имеет размер а на длину tmp
      for (int i = 0; i < result.length; i++) {               // result.length - длина массива определяется по его первой размерности
        for (int j = 0; j < result[0].length; j++) {
          result[i][j] = a.get(i)[j];                         // в цикле записываем в матрицу-результат i-j элемент из списочно-расш массива
        }
      }
      MainMatrix = result;
      this.r = result.length;
      this.c = result[0].length;
    } catch (IOException e) {
      e.printStackTrace();
    }

  }


  public DenseMatrix(double[][] matr)      //матрица по экземпляру
  {
    this.MainMatrix = matr;
    this.r = matr.length;
    this.c = matr[0].length;
  }

  public DenseMatrix(int length, int hight){
    this.c = length;
    this.r = hight;
    MainMatrix = new double[hight][length];
  }
  /**
   * однопоточное умножение матриц
   * должно поддерживаться для всех 4-х вариантов
   *
   * @param o
   * @return
   */
  @Override
  public Matrix mul(Matrix o) {
    if (o instanceof DenseMatrix) {
      return mul((DenseMatrix) o);
    }
    if (o instanceof SparseMatrix) {
      return mul((SparseMatrix) o);
    } else return null;
  }

  private DenseMatrix mul(DenseMatrix dM) {

    int m = r;
    int n = dM.c;
    int p = dM.r;

    double[][] res = new double[m][n];

    for (int i = 0; i < m; i++) {
      for (int j = 0; j < n; j++) {
        for (int k = 0; k < p; k++) {
          res[i][j] += MainMatrix[i][k] * dM.MainMatrix[k][j];
        }
      }
    }
    return new DenseMatrix(res);
  }

  private SparseMatrix mul(SparseMatrix Smatr) //умножение на sparse
  {
    if (c != ((SparseMatrix) Smatr).hight) {
      return (null);
    }
    SparseMatrix res = new SparseMatrix(r, ((SparseMatrix) Smatr).length);
    DenseMatrix trans = this.transp();                                            //транспонируем и умножаем как SD
    for (Point key : ((SparseMatrix) Smatr).sMatrix.keySet()) {
      for (int i = 0; i < ((SparseMatrix) Smatr).length; i++){
        Point q = new Point(i, key.y);
        if (res.sMatrix.containsKey(q))
        {
          double t = res.sMatrix.get(q) + ((SparseMatrix) Smatr).sMatrix.get(key)*trans.MainMatrix[key.x][i];
          res.sMatrix.put(q, t);
        } else {
          double t = ((SparseMatrix) Smatr).sMatrix.get(key)*trans.MainMatrix[key.x][i];
          res.sMatrix.put(q, t);
        }
      }
    }
    res.sMatrix.entrySet().removeIf(entry -> Math.abs(entry.getValue()) < 1.0E-06);
    return res;


  }


  /**
   * многопоточное умножение матриц
   *
   * @param o
   * @return
   */
  @Override public Matrix dmul(Matrix o)
  {
    if (o instanceof DenseMatrix) {
      if (this.r != ((DenseMatrix) o).c) {
        return (null);
      }
      DenseMatrix res = new DenseMatrix(this.r, ((DenseMatrix) o).c);
      DenseMatrix trans = ((DenseMatrix) o).transp();               //
      ArrayList<Thread> t = new ArrayList<>();                      //массив всех потоков
      int str2 = this.r/4 + 1;                                        //сколько строк обратывать одному потоку
      for (int i = 0; i < res.r; i+=str2) {                           //считаем с каокой строки начанить умножать
        Invest act = new Invest(i, str2, this, trans, res);        //создаем act типа invest
        Thread temp = new Thread(act);                              //создаем поток с функцией акт
        t.add(temp);                                                //добавляем новый поток к листу всех потоков
        temp.start();                                               //запуск потока
      }
      for (Thread p: t) {                                           //пробегаем по всему лисут и ждем пока все закончатся
        try {
          p.join();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      return (res);
    }
    return null;
  }


  /**
   * спавнивает с обоими вариантами
   *
   * @param o
   * @return
   */
  /*Сравнивает две матрицы на совпадение*/
  @Override
  public boolean equals(Object o) {
    if (o instanceof DenseMatrix) {     //сравнение с dense
      DenseMatrix O = (DenseMatrix) o;
      int i, j;
      if ((r == O.r) && (c == O.c)) {
        for (i = 0; i < r; ++i)
          for (j = 0; j < c; ++j)
            if (MainMatrix[i][j] != O.MainMatrix[i][j]){
              return false;
            }
      }
      return true;
    }
    else return false;
  }
  private DenseMatrix transp(){
    DenseMatrix res = new DenseMatrix(r,c);
    for (int i = 0; i <c; i++){
      for (int j = 0; j < r; j++){
        res.MainMatrix[i][j] = MainMatrix[j][i];
      }
    }
    return (res);
  }

}