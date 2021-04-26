public class Test1 {


  public class Interval {
    int start;
    int end;
    public Interval(int start, int end) {
      this.start = start;
      this.end = end;
    }
  }
    /**
     * 代码中的类名、方法名、参数名已经指定，请勿修改，直接返回方法规定的值即可
     *
     * 计算探险游戏的最高得分
     * @param m int整型 层数
     * @param points int整型二维数组 每层据点分值
     * @param clues Interval类二维数组 每层据点线索，第k行的[i, j]表示k层i据点可通往k+1层j据点
     * @return int整型
     */
    public int solve (int m, int[][] points, Interval[][] clues) {
        // write code here
        int score = 0;
        for(int k = 0 ; k < clues.length ; k++) {
            // Interval[] [[0,0],[0,1],[2,1]]
            for (int i = 0; i < clues[0].length ; i++) {

            }
        }
        return score;
    }
}
