public class QuickSort {
    public static void main(String[] args) {
        int[] nums = new int[]{3,1,2,5,7,8,3,2,};
        sort(nums,0,nums.length-1);
        for (int num : nums) {
            System.out.println(num);
        }

    }

    public static void sort(int[] a, int left, int right) {
        if(left>=right) return;;
        int i = left , j = right , temp = a[left];
        while(i<j) {
            while(i<j && a[j]>temp) j--;
            if(i<j) a[i++] = a[j];
            while (i<j && a[i]<temp) i++;
            if(i<j) a[j--] = a[i];
        }
        a[i] = temp;
        sort(a,left,i-1);
        sort(a,i+1,right);
    }
}
