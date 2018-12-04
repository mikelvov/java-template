package edu.spbu.matrix;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Разряженная матрица
 */

class SInvest implements Runnable{

    private int str1, str2;
    private Point key;
    private SparseMatrix a, b, res;
    SInvest(int k, int l, SparseMatrix i, SparseMatrix j, SparseMatrix c){
        res = c;
        str1 = k;
        str2 = l;
        a = i;
        b = j;
    }
    @Override
    public void run() {
        for (Point key: a.sMatrix.keySet()) {             //по всем ключам
            for (int i = str1; i < str1 + str2; i++) {    // ограничиваемся по строкам второй матрицы
                if (i < b.length) {
                    Point p = new Point(key.y, i);
                    if (b.sMatrix.containsKey(p)) {
                        Point q = new Point(key.x, p.y);
                        if (res.sMatrix.containsKey(q)) {
                            double t = res.sMatrix.get(q) + a.sMatrix.get(key) * b.sMatrix.get(p);
                            res.sMatrix.put(q, t);
                        } else {
                            double t = a.sMatrix.get(key) * b.sMatrix.get(p);
                            res.sMatrix.put(q, t);
                        }
                    }
                }
            }
        }
    }
}

public class SparseMatrix implements Matrix
{
    int length, hight;
    HashMap<Point, Double> sMatrix;                                  //point(i,j) - позиция в матрице

    public SparseMatrix(int length, int hight){               //конструктор по длине и ширине
        this.length = length;
        this.hight = hight;
        sMatrix = new HashMap<>();
    }

    public SparseMatrix(int length, int hight, double[][] elem) {     //конструктор по длине ширине и массиву
        this.length = length;
        this.hight = hight;
        sMatrix = new HashMap<>();
        for (int i = 0; i < hight; i++){
            for (int j = 0; j < length; j++){
                if (elem[i][j] != 0)
                {
                    sMatrix.put(new Point(i,j), elem[i][j]);
                }
            }
        }
    }
    /**
     * загружает матрицу из файла
     *
     * @param fileName
     */
    public SparseMatrix(String fileName) {    //считывем из файла
        try {
            File f = new File(fileName);
            Scanner input = new Scanner(f);
            String[] line;
            Double[] temp = {};
            int check = 0, str = 0;
            sMatrix = null;
            if (input.hasNextLine()) {                                  //check проверка на длину строки
                line = input.nextLine().split(" ");
                check = line.length;
                sMatrix = new HashMap<>();
                temp = new Double[check];
                for (int i = 0; i < check; i++) {
                    temp[i] = Double.parseDouble(line[i]);
                    if (temp[i] != 0) {
                        sMatrix.put(new Point(0, i), temp[i]);
                    }
                }
                str++;
            }
            while (input.hasNextLine()) {
                line = input.nextLine().split(" ");
                if (check != line.length) {
                    throw new IOException("Неверная размерность матрицы.");
                }
                temp = new Double[check];
                for (int i = 0; i < check; i++) {
                    temp[i] = Double.parseDouble(line[i]);
                    if (temp[i] != 0) {
                        sMatrix.put(new Point(str, i), temp[i]);
                    }
                }
                str++;
            }
            this.hight = str;
            this.length = check;
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла.\n" + e.getMessage());
        }

    }

    /**
     * однопоточное умножение матриц
     * должно поддерживаться для всех 4-х вариантов
     *
     * @param o
     * @return
     */
    @Override
    public SparseMatrix mul(Matrix o) {
        if (o instanceof SparseMatrix) {
            if (length != ((SparseMatrix) o).hight) {
                return (null);
            }
            SparseMatrix res = new SparseMatrix(((SparseMatrix) o).length, hight);
            for (Point key : sMatrix.keySet()) {                                             //по всем поинтам первой матрицы
                for (int i = 0; i < ((SparseMatrix) o).length; i++) {
                    Point p = new Point(key.y, i);                                           //поинт p
                    if (((SparseMatrix) o).sMatrix.containsKey(p)) {
                        Point q = new Point(key.x, p.y);
                        if (res.sMatrix.containsKey(q)) {                                    //если в результирующей есть элемент на месте q
                            double t = res.sMatrix.get(q) + sMatrix.get(key) * ((SparseMatrix) o).sMatrix.get(p);
                            res.sMatrix.put(q, t);                                          //добавляем значение
                        } else {
                            double t = sMatrix.get(key) * ((SparseMatrix) o).sMatrix.get(p);
                            res.sMatrix.put(q, t);                                          //если элемента нет то сразу добавляем
                        }
                    }
                }
            }
            res.sMatrix.entrySet().removeIf(entry -> Math.abs(entry.getValue()) < 1.0E-06);     //если какой-то элементы результирующей меньше 10^(-6) то его удаляем
            return res;
        }



        if (o instanceof DenseMatrix) {
            if (length != ((DenseMatrix) o).r) {
                return (null);
            }
            SparseMatrix res = new SparseMatrix(((DenseMatrix) o).c, hight);
            for (Point key : sMatrix.keySet()) {
                for (int i = 0; i < ((DenseMatrix) o).r; i++){
                    Point q = new Point(key.y, i);
                    if (res.sMatrix.containsKey(q))
                    {
                        double t = res.sMatrix.get(q) + sMatrix.get(key)*((DenseMatrix) o).MainMatrix[key.y][i];
                        res.sMatrix.put(q, t);
                    } else {
                        double t = sMatrix.get(key)*((DenseMatrix) o).MainMatrix[key.y][i];
                        res.sMatrix.put(q, t);
                    }
                }
            }
            res.sMatrix.entrySet().removeIf(entry -> Math.abs(entry.getValue()) < 1.0E-06);
            return res;
        }
        return(null);
    }

    /**
     * многопоточное умножение матриц
     *
     * @param o
     * @return
     */
    @Override
    public Matrix dmul(Matrix o) {                               //делим на 4 части и помот складываем все совпавшие значения
        if (o instanceof SparseMatrix) {                        //если бы все записывали то из разных потоков могли бы не совпаддать элемент
            if (length != ((SparseMatrix) o).hight) {           //в разных потоках могли бы вычислить один и тот же элемент
                return (null);
            }
            SparseMatrix res = new SparseMatrix(((SparseMatrix) o).length, hight);
            ArrayList<Thread> t = new ArrayList<>();
            ArrayList<SparseMatrix> R = new ArrayList<>();                        //лист матриц
            int str2 = ((SparseMatrix) o).length / 4 + 1;
            for (int i = 0; i < ((SparseMatrix) o).length; i += str2) {
                SparseMatrix Re = new SparseMatrix(((SparseMatrix) o).length, hight);
                R.add(Re);
                SInvest act = new SInvest(i, str2, this, (SparseMatrix) o, Re);
                Thread temp = new Thread(act);
                t.add(temp);
                temp.start();
            }
            for (Thread p : t) {
                try {
                    p.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (SparseMatrix r: R) {                       //по всем матрицам делаем одну
                for (Point key : r.sMatrix.keySet()) {      //по всем ключам матрицы
                    if (res.sMatrix.containsKey(key))       //если в итоговой уже есть такой ключ
                    {
                        double tem = res.sMatrix.get(key) + r.sMatrix.get(key);  //складываем эти элементы
                        if (Math.abs(tem) < 1.0E-06)
                        {
                            res.sMatrix.remove(key);
                        } else {
                            res.sMatrix.put(key, tem);
                        }
                    } else {
                        if (Math.abs(r.sMatrix.get(key)) >= 1.0E-06)
                        {
                            res.sMatrix.put(key, r.sMatrix.get(key));
                        }
                    }
                }
            }
            res.sMatrix.entrySet().removeIf(entry -> Math.abs(entry.getValue()) < 1.0E-06);
            return res;
        }
        return null;
    }
    /**
     * сравнивает с обоими вариантами
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SparseMatrix)) {
            return (false);
        }
        if ((length != ((SparseMatrix) o).length) || (hight != ((SparseMatrix) o).hight)|| (sMatrix.size() != ((SparseMatrix) o).sMatrix.size())){
            return (false);
        }
        for (Point key : sMatrix.keySet()) {
            if (!((SparseMatrix) o).sMatrix.containsKey(key))
                return (false);
            if (Math.abs(sMatrix.get(key) - ((SparseMatrix) o).sMatrix.get(key)) >= 1.0E-06){
                return(false);
            }
        }
        return (true);
    }

    @Override
    public String toString() {
        double[][] temp = new  double[hight][length];
        for (Map.Entry<Point, Double> pointDoubleEntry : sMatrix.entrySet()){
            temp[pointDoubleEntry.getKey().x][pointDoubleEntry.getKey().y] = pointDoubleEntry.getValue();
        }
        StringBuilder dat = new StringBuilder();
        for (int i = 0; i < hight; i++){
            dat.append(Arrays.toString(temp[i]));
            dat.append("\n");
        }
        return (dat.toString());
    }
}