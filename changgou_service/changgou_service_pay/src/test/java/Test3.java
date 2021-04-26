import java.util.*;

public class Test3 {
    /**
     * 代码中的类名、方法名、参数名已经指定，请勿修改，直接返回方法规定的值即可
     *
     * 检查输入的国家是否在同一大陆
     * @param num int整型 国家数量
     * @param country int整型二维数组 相连国家
     * @param check int整型二维数组 待判断国家
     * @return bool布尔型一维数组
     */
    static Set<Integer> path;
    static  boolean[] res;
    public static boolean[] mainlandCheck (int num, int[][] country, int[][] check) {
        // write code here
        res = new boolean[check.length];
        if (check.length == 0) return null;
        for (int i = 0; i < check.length; i++) {
            path = new HashSet<>();
            pathCheck(country,check[i][0],check[i][1],0,0);
            boolean flag = false;
            for (int[] cont : country) {
                if (cont[0] == check[i][0] && cont[1] == check[i][1]) {
                    flag = true;
                    break;
                }
                if (cont[0] == check[i][1] && cont[1] == check[i][0]) {
                    flag = true;
                    break;
                }
            }
            if (path.size()>2 && path.contains(check[i][1])) {
                res[i] = true;
            }else if (flag) {
                res[i] = true;
            }else {
                res[i] = false;
            }
        }
        return res;
    }

    public static void pathCheck(int[][] country,int start, int end, int preStart, int preEnd){
        if (start == end) return;
        if (start == preEnd && end == preStart) return;;
        path.add(start);
        preStart = start;
        preEnd = end;
        start = end;
        for (int[] cont : country) {
            int first = cont[0];
            int second = cont[1];
            if(first == end) {
                end = second;
                pathCheck(country,start,end,preStart,preEnd);
            }
            if(second == end) {
                end = first;
                pathCheck(country,start,end,preStart,preEnd);
            }
        }
    }

    public static void main(String[] args) {
        int[][] country = new int[][]{{1,4},{2,3},{3,5},{6,7},{2,4},{8,0}};
        int[][] check = new int[][]{{1,5},{6,8},{1,8}};
        boolean[] booleans = mainlandCheck(9, country, check);
        for (boolean aBoolean : booleans) {
            System.out.println(aBoolean);
        }
    }
}
