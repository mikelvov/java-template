package edu.spbu.sort;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by artemaliev on 07/09/15.
 */
public class IntSort {
  public static void mergeSort(int[] a, int low, int high) {
    if (low + 1 < high) {
      int mid = (low + high)/2;
      mergeSort(a, low, mid);
      mergeSort(a, mid, high);

      int size = high - low;
      int[] b = new int[size];
      int i = low;
      int j = mid;
      for (int k = 0; k < size; k++) {
        if (j >= high || i < mid && a[i] < a[j]) {
          b[k] = a[i++];
        } else {
          b[k] = a[j++];
        }
      }
      System.arraycopy(b, 0, a, low, size);
    }

  }
  public static void sort (List<Integer> list)
  {
    Collections.sort(list);
  }
}
