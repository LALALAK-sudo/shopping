import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class testAli {
    private static List<List<Integer>> res = new ArrayList<>();
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int m = sc.nextInt();
        int n = sc.nextInt();
        List<Character> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            list.add(sc.next().charAt(0));
           // System.out.println(list.get(i));
        }
       // System.out.println(Integer.valueOf(list.get(0)));
        List<Integer> list2 = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            list2.add(Integer.valueOf(list.get(i)));
        }
        Collections.sort(list2);
        List<Integer> temp = new ArrayList<>();
        int[] used = new int[n];
        backTrack(used,temp, list2,m, n,0);

        for (List<Integer> r : res) {
            for (int i = 0; i < r.size(); i++) {
                System.out.print( (char) (int)r.get(i) );
            }
            System.out.println("  ");
        }
    }

    public static void backTrack(int[] used, List<Integer> temp,List<Integer> list, int m, int n,int index) {
        if(temp.size() == m) {
            res.add(new ArrayList<Integer>(temp));
            return;
        }
        for (int i = 0; i < n; i++) {
            if(used[i] == 0) {
                if(temp.isEmpty()) {
                    temp.add(list.get(i));
                    index++;
                }
                if(index>=1) {
                    Integer num1 = temp.get(index - 1);
                    Integer num2 = list.get(i);
                    if(num2 > num1) {
                        temp.add(list.get(i));
                        used[i] = 1;
                        index++;
                        backTrack(used,temp,list,m,n,index);
                        used[i] = 0;
                        index--;
                        temp.remove(temp.size()-1);
                    }
                }
            }
        }
    }
}
