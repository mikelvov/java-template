package edu.spbu.matrix;
import java.util.HashMap;
import java.io.File;
import java.util.Scanner;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Плотная матрица
 */
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
        //на каждом шаге есть очередная строка (line), которую преобразуем к массиву из чисел (tmp), а потом зная их итоговые размеры собираеи всё в двумерный массив (rezult), который приравнивается к итоговому значению матрицы
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

  private DenseMatrix mul(SparseMatrix Smatr) //умножение на sparse
  {
    SparseMatrix sT = Smatr.transpose();
    double[][] result = new double[r][Smatr.c];
    double sum = 0;
    for (int i = 0; i<r; i++){
      for (HashMap.Entry<Integer, HashMap<Integer, Double>> row2 : sT.MainMatrix.entrySet()) {
        for (int k = 0; k<c; k++) {
          if (row2.getValue().containsKey(k)) {
            sum += MainMatrix[i][k]*row2.getValue().get(k);
          }
        }
        result[i][row2.getKey()] = sum;
        sum = 0;
      }
    }
    return new DenseMatrix(result);
  }


  /**
   * многопоточное умножение матриц
   *
   * @param o
   * @return
   */
  @Override
  public Matrix dmul(Matrix o) {
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


}