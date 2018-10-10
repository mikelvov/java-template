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


}