package edu.spbu.sort;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by artemaliev on 07/09/15.
 */
public class IntSort {
  public static void sort1 (int array[],int l, int r) {
    if (l < r) {
      int mid = (l + r) / 2;
      sort1(array, l, mid);
      sort1(array, mid + 1, r);

      int[] b = Arrays.copyOf(array, array.length);
      for (int k = l; k <= r; k++)
        b[k] = array[k];

      int i = l;
      int j = mid;
      for (int n = l; n <= r; n++) {
        if (i > mid) {
          array[n] = b[j];
          j++;
        } else if (j > r) {
          array[n] = b[i];
          i++;
        } else if (b[j] < b[i]) {
          array[n] = b[j];
          j++;
        } else {
          array[n] = b[i];
          i++;
        }
      }
    }
  }
  public static void sort (List<Integer> list) {
    Collections.sort(list);
  }
}
