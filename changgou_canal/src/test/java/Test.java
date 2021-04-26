import java.util.HashMap;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        int[] a = {3,4,2,3,5,3,4,5};
        quickSort(a,0,a.length-1);
        for (int i : a) {
            System.out.println(i);
        }
    }

    public static void quickSort(int[] a, int left, int right) {
        if (left>=right) return;
        int i = left , j = right , temp = a[left];
        while (i<j) {
            while (i<j && a[j]>temp) j--;
            if(i<j) a[i++] = a[j];
            while (i<j && a[i]<temp) i++;
            if(i<j) a[j--] = a[i];
        }
        a[i] = temp;
        quickSort(a,left,i-1);
        quickSort(a,i+1,right);
    }
}
