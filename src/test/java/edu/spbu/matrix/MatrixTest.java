package edu.spbu.matrix;

import org.junit.Test;
import java.io.*;
import static org.junit.Assert.assertEquals;

public class MatrixTest
{
  /**
   * ожидается 4 таких теста
   */

  @Test
  public void mulDD() throws IOException {
    Matrix m1 = new DenseMatrix("m1.txt");
    Matrix m2 = new DenseMatrix("m2.txt");
    Matrix expected = new DenseMatrix("resultm1m2.txt");
    assertEquals(expected, m1.mul(m2));
  }
  @Test
  public void mulSS() throws IOException {
    Matrix m1 = new SparseMatrix("m1.txt");
    Matrix m2 = new SparseMatrix("m2.txt");
    Matrix expected = new SparseMatrix("resultm1m2.txt");
    assertEquals(expected, m1.mul(m2));
  }
  @Test
  public void mulDS() throws IOException {
    Matrix m1 = new DenseMatrix("m1.txt");
    Matrix m2 = new SparseMatrix("m2.txt");
    Matrix expected = new SparseMatrix("resultm1m2.txt");
    assertEquals(expected, m1.mul(m2));
  }
  @Test
  public void mulSD() throws IOException {
    Matrix m1 = new SparseMatrix("m1.txt");
    Matrix m2 = new DenseMatrix("m2.txt");
    Matrix expected = new SparseMatrix("resultm1m2.txt");
    assertEquals(expected, m1.mul(m2));
  }

  @Test
  public void dmulDD() throws IOException {
    Matrix m1 = new DenseMatrix("m1.txt");
    Matrix m2 = new DenseMatrix("m2.txt");
    Matrix expected = new DenseMatrix("resultm1m2.txt");
    assertEquals(expected, m1.mul(m2));
  }
  @Test
  public void dmulSS() throws IOException {
    Matrix m1 = new SparseMatrix("m1.txt");
    Matrix m2 = new SparseMatrix("m2.txt");
    Matrix expected = new SparseMatrix("resultm1m2.txt");
    assertEquals(expected, m1.mul(m2));
  }




}