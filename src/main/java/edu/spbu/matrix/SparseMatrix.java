package edu.spbu.matrix;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Разряженная матрица
 */
public class SparseMatrix implements Matrix {
  public HashMap<Integer, HashMap<Integer, Double>> MainMatrix; // Матрица в формате
  public int r;
  public int c;

  public SparseMatrix(String fileName)   //матрица по файлу
  {
    try {
      r = 0;                                    //храним строку матрицы как номер ненулевого элемента и его значение (пара ключ/значение)
      c = 0;
      HashMap<Integer, HashMap<Integer, Double>> res = new HashMap<Integer, HashMap<Integer, Double>>();
      File f = new File(fileName);
      Scanner input = new Scanner(f);
      String[] line = {};
      HashMap<Integer, Double> buf = null;            // buf для считывания строк матрицы  (можно убрать null)


      int k = 0;
      if (input.hasNextLine()) {                      //делаем проверку на размер матрицы
        line = input.nextLine().split(" ");
        k = line.length;
        buf = new HashMap<Integer, Double>();
        for (int i = 0; i < line.length; i++) {
          if (line[i] != "0") {
            buf.put(i, Double.parseDouble(line[i]));    // если элемент не ноль, то записываем в buf его номер и значение
          }
        }
        if (buf.size() != 0) {
          res.put(r++, buf);            //если #элементов в buf не ноль, то записываем в результат
        }
      }

      while (input.hasNextLine()) {
        // buf = new HashMap<Integer, Double>();
        line = input.nextLine().split(" ");
        if (k != line.length) {
          throw new IOException("Неверный размер матрицы");
        }
        for (int i = 0; i < line.length; i++) {
          if (line[i] != "0") {
            buf.put(i, Double.parseDouble(line[i]));    // если элемент не ноль, то записываем в buf его номер и значение
          }
        }
        if (buf.size() != 0) {
          res.put(r++, buf);            //если #элементов в buf не ноль, то записываем в результат
        }
      }
      c = line.length;
      MainMatrix = res;
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public SparseMatrix(HashMap<Integer, HashMap<Integer, Double>> matrix, int row, int column) //контструктор по экземпляру
  {
    this.MainMatrix = matrix;
    this.r = row;
    this.c = column;
  }


  /**
   * однопоточное умнджение матриц
   * должно поддерживаться для всех 4-х вариантов
   *
   * @param o
   * @return
   */
  @Override
  public Matrix mul(Matrix o)//однопоточное умножение
  {
    if (o instanceof DenseMatrix) {
      return mul((DenseMatrix) o);
    }
    if (o instanceof SparseMatrix) {
      return mul((SparseMatrix) o);
    } else
      {
        return null;
      }
  }

  public SparseMatrix transpose()//транспонирование sparse
  {
    HashMap<Integer, HashMap<Integer, Double>> result = new HashMap<Integer, HashMap<Integer, Double>>();
    for (HashMap.Entry<Integer, HashMap<Integer, Double>> row : MainMatrix.entrySet()) {    //Entry - пройдем по всем парам ключ-значение
      for (HashMap.Entry<Integer, Double> elem : row.getValue().entrySet()) {               //по всем значениям в row     //      entrySet возвращает набор ключ значений
        if (!result.containsKey(elem.getKey())) {        //Если в рез. матр. нет ключа как ключ elem, то добавляем ключ этого элемента как ключ хэшмапа ( то есть как номер строки) ( если элемент был 1,3 позиции, то станет 3,1)
          result.put(elem.getKey(), new HashMap<Integer, Double>());
        }
        result.get(elem.getKey()).put(row.getKey(), elem.getValue());                       //Дальше в строке "ключ элемента" добавлем в новый хэш мап номер текущей строки и само значение
      }
    }
    return new SparseMatrix(result, c, r);
  }
// умножение на спарсе










  private SparseMatrix mul(SparseMatrix Smatr)             //умножение на sparse
  {
    HashMap<Integer, HashMap<Integer, Double>> res = new HashMap<Integer, HashMap<Integer, Double>>();
    SparseMatrix sT = Smatr.transpose();                                                         //  Транспонируем на что умножаем
    double buf = 0;
    for (HashMap.Entry<Integer, HashMap<Integer, Double>> row1 : MainMatrix.entrySet()) {        //  В цикле по парам ключ-значение исходной матрицы (row1)
      for (HashMap.Entry<Integer, HashMap<Integer, Double>> row2 : sT.MainMatrix.entrySet()) {   //и парам трансп.матр. (row2)
        for (HashMap.Entry<Integer, Double> elem : row1.getValue().entrySet()) {                 //и по значениям в (row1)
          if (row2.getValue().containsKey(elem.getKey())) {                                      //Если у значения в (row2) есть ключ как ключ элемента в (row1)
            buf += elem.getValue() * row2.getValue().get(elem.getKey());                         //, то в buf записываем умножение эл-та из (row1) на эл-т из (row2) с таким же ключом
          }
        }

        if (buf != 0) {                                               // если получилось не 0
          if (!res.containsKey(row1.getKey())) {                      //и результат не содержит ключа как ключ первой строки,
            res.put(row1.getKey(), new HashMap<Integer, Double>());   // то добавляем в рез. ключ первой строки как ключ хешмапа
          }
          res.get(row1.getKey()).put(row2.getKey(), buf);             //  Добавляем в строку с ключом (row1) ключ второй строки ((row2) ) и значение buf
        }
        buf = 0;                                                     //  Обнуляем buf
      }
    }
    return new SparseMatrix(res, r, Smatr.c);
  }


  private DenseMatrix mul(DenseMatrix Dmatr) // умножение на dense
  {

    double[][] result = new double[r][Dmatr.c];
    double sum = 0;
    for (Map.Entry<Integer, HashMap<Integer, Double>> row1 : MainMatrix.entrySet()){       // В цикле по парам ключ-значение исходной матрицы (row1)
      for (int j = 0; j<Dmatr.r; j++) {                                                    // от 0 до кол-ва столбцов второй
        for (HashMap.Entry<Integer, Double> elem : row1.getValue().entrySet()) {           // и по значениям в (row1)
          if (row1.getValue().containsKey(elem.getKey())) {                                //Если у значения в (row1) есть ключ как ключ элемента в (row1)
            sum += elem.getValue()*Dmatr.MainMatrix[elem.getKey()][j];                     // то в sum записываем умножение
          }
        }
        result[row1.getKey()][j] = sum;                                                    // в новой матрице элемент sum
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
  @Override
  public boolean equals(Object o)//сравнение
  {
    if (o instanceof DenseMatrix) //сравнение  с dense
    {
      DenseMatrix tmp = (DenseMatrix)o;
      if (tmp.MainMatrix.length == r && tmp.MainMatrix[0].length == c) {
        for (int i = 0; i<r; i++) {
          if (MainMatrix.containsKey(i)) {
            for (int j = 0; j<c; j++) {
              if (MainMatrix.get(i).containsKey(j)) {
                if (MainMatrix.get(i).get(j) != tmp.MainMatrix[i][j]) {
                  return false;
                }
              } else {
                if (tmp.MainMatrix[i][j] != 0) {
                  return false;
                }
              }
            }
          } else {
            for (int j = 0; j < c; j++) {
              if (tmp.MainMatrix[i][j] != 0) {
                return  false;
              }
            }
          }
        }
      } else {
        return false;
      }
    } else if (o instanceof SparseMatrix) // сравнение с sparse
    {
      SparseMatrix tmp = (SparseMatrix) o;
      if (tmp.c == c && tmp.r == r) {
        for (int i = 0; i<r; i++) {
          if (MainMatrix.containsKey(i) && tmp.MainMatrix.containsKey(i))  {
            for (int j = 0; j<c; j++) {
              if (MainMatrix.get(i).containsKey(j) && tmp.MainMatrix.get(i).containsKey(j)) {
                if (MainMatrix.get(i).get(j).doubleValue() != tmp.MainMatrix.get(i).get(j).doubleValue()) {
                  return false;
                }
              } else if (MainMatrix.get(i).containsKey(j) || tmp.MainMatrix.get(i).containsKey(j)) {
                return false;
              }
            }
          } else if (MainMatrix.containsKey(i) || tmp.MainMatrix.containsKey(i)) {
            return false;
          }
        }
      }
      else {
        return false;
     }
    }
    return true;
  }
}


