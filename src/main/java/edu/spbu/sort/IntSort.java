package edu.spbu.sort;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by artemaliev on 07/09/15.
 */
public class IntSort {
  public static void sort1 (int array[],int l, int r) {
    if (l + 1 < r) {
      int mid = (l + r)/2;
     sort1(array, l, mid);
     sort1(array, mid, r);

      int size = r - l;
      int[] b = new int[size];
      int i = l;
      int j = mid;
      for (int n = 0; n < size; n++) {
        if (j >= r || i < mid && array[i] < array[j]) {
          b[n] = array[i++];
        } else {
          b[n] = array[j++];
        }
      }
      System.arraycopy(b, 0, array, l, size);
    }
  }

  public static void sort (List<Integer> list) {
    Collections.sort(list);
  }
}
