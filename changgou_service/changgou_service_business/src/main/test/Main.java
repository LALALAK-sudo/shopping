import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String n = scanner.nextLine();
        String s = scanner.nextLine();
        System.out.println(s);
        List<String> pack = new ArrayList<>();
        String next = scanner.next();
        pack.add(next);
        while (next != null && !next.equals("")) {
            System.out.println(pack);
            next = scanner.next();
            pack.add(next);
        }
        System.out.println(111111);
        System.out.println(pack);
        int[] price = new int[pack.size()];
        for (int i = 0; i < pack.size(); i++) {
            price[i] = scanner.nextInt();
        }
        List<Integer> value = new ArrayList<>();
        while(scanner.hasNext()) {
            value.add(scanner.nextInt());
        }
        List<List<Integer>> packAge = new ArrayList<>();
        for (int i = 0; i < pack.size(); i++) {
            String str = pack.get(i);
            List<Integer> pa = new ArrayList<>();
            String[] strs = str.split(",");
            for (String s1 : strs) {
                pa.add(Integer.valueOf(s1));
            }
            packAge.add(pa);
        }
        // 找到希望的packAge的组合
        List<Integer> resEnable = new ArrayList<>();
        List<Integer> allRes = new ArrayList<>();
        reCur(packAge,value,0);
        for (Set<Integer> re : res) {
            for (Integer integer : re) {
                System.out.print(integer);
            }
            System.out.println("   ");
        }
//        for (List<Integer> integers : packAge) {
//
//        }

    }

    public static List<Integer> getResEnable(List<List<Integer>> packAge,List<Integer> value) {
        List<Integer> resEnable = new ArrayList<>();
        List<Integer> allRes = new ArrayList<>();
        // Map<Integer,List<Integer>> all = new HashMap<>();
        int n = packAge.size();
        for(int i = 0 ; i < n ; i++) {
            for(int j = i ; j < n ;j++) {
                int temp = j;
                allRes.addAll(packAge.get(i));
                while(temp != i) {
                    allRes.addAll(packAge.get(temp));
                    temp--;
                }
                if(allRes.contains(value)) {
                   // resEnable.add()
                }
            }
        }
        return resEnable;
//        // 包含第i个权益包
//        for (List<Integer> integers : packAge) {
//            List<Integer> temp = new ArrayList<>();
//            temp = value;
//
//        }
    }
    private static Set<Integer> resEnable1 = new HashSet<>();
    private static List<Set<Integer>> res = new ArrayList<>();
    public static void reCur(List<List<Integer>> packAge,List<Integer> value,int index) {

        // 包含第i个权益包
        for (List<Integer> integers : packAge) {
            List<Integer> temp = new ArrayList<>();
            temp = value;
            int j = 0;
            while(j != value.size()-1) {
                for (int i = 0; i < integers.size(); i++) {
                    if(integers.get(i) == temp.get(j)) {
                        resEnable1.add(index);
                        temp.remove(j);
                    }
                }
                j++;
            }
            if(!temp.isEmpty()) {
                packAge.remove(integers);
                index++;
                reCur(packAge,temp,index);
            }
            res.add(resEnable1);
            return;
        }
    }
}
