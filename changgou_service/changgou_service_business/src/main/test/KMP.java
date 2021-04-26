import java.lang.reflect.Array;
import java.util.Arrays;

public class KMP {
    public static void main(String[] args){
        String a = "ABACABAD";
        String b = "BBC ABACABACABAD ABCDABDE";
        int result = kmp(b, a);

        //打印结果：和字符串获得匹配的位置
        System.out.println("resultPosition:"+result);
    }

    private static int kmp(String str, String dest) {
        int[] next = kmpNext(dest);
        System.out.println("next = " + Arrays.toString(next));
        for (int i = 0 , j = 0 ;  i < str.length() ; i++) {
            while(j > 0 && str.charAt(i) != dest.charAt(j)) {
                j = next[j-1];
            }
            if(str.charAt(i) == dest.charAt(j)) {
                j++;
            }
            if( j == dest.length()) {
                return i-j+1;
            }
        }
        return -1;
    }

    private static int[] kmpNext(String dest) {
        int[] next = new int[dest.length()];
        next[0] = 0;
        for (int i = 1, j = 0; i < dest.length(); i++) {
            while(j>0 && dest.charAt(j) != dest.charAt(i)) {
                j = next[j-1];
            }
            if(dest.charAt(i) == dest.charAt(j)) {
                j++;
            }
            next[i] = j;
        }
        return next;
    }
}
