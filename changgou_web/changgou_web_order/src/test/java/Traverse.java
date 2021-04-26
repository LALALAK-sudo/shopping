import java.util.Arrays;

public class Traverse {
    public static void main(String[] args) {
        String s = "the sky is blue";
        String[] strs = s.split(" ");
        String[] res = new String[strs.length];
        int n = strs.length;
        n--;
        for(String str : strs ) {
            res[n--] = str;
        }
        String str = Arrays.toString(res).trim();

        System.out.println(str);
    }
}
