import java.util.*;

public class Test2 {
    /**
     * 代码中的类名、方法名、参数名已经指定，请勿修改，直接返回方法规定的值即可
     *
     * 用户名搜索
     * @param users string字符串一维数组 入库的用户名
     * @param hot int整型一维数组 对应用户名的热度值，数组长度与users相同
     * @param keyword string字符串 搜索的用户名
     * @return string字符串ArrayList<ArrayList<>>
     */
    public static ArrayList<ArrayList<String>> search (String[] users, int[] hot, String keyword) {
        // write code here
        ArrayList<ArrayList<String>> res = new ArrayList<>();
        String temp = "";
        for (int i = 0; i < keyword.length(); i++) {
            ArrayList<String> list = new ArrayList<>();
            //temp = "j" or "ja"
            temp += keyword.charAt(i);
            Map<Integer,String> map = new HashMap<>();
            for(int j = 0 ; j < users.length ; j++) {
                if(users[j].length() >= i+1) {
                    String substring = users[j].substring(0, i + 1);
                    if(substring.equals(temp)) {
                        map.put(hot[j],users[j]);
                    }
                }
            }
            Set<Integer> keySet = map.keySet();
            List<Integer> t = new ArrayList<>(keySet);
            Collections.sort(t);
            Collections.reverse(t);
            int count = 0;
            while(count<3) {
                if(count >= t.size()) {
                    break;
                }
                list.add(map.get(t.get(count)));
                count++;
            }
            if (list != null && list.size()>0) {

                res.add(list);
            }
        }
        return res;
    }

    public static void main(String[] args) {
//        Map map = new HashMap();
//        map.put(1,"1");
//        map.put(2,"2");
//        map.put(3,"3");
//        map.put(4,"4");
//        Set<Integer> keySet = map.keySet();
//        List<Integer> t = new ArrayList<>(keySet);
//        Collections.sort(t);
//        Collections.reverse(t);
//        System.out.println(t);
        String[] users = new String[]{"just","java","jar","jack","jackson"};
        int[] hot = new int[]{1,5,3,4,2};
        ArrayList<ArrayList<String>> jack = search(users, hot, "jack");
        System.out.println(jack);

    }


}
