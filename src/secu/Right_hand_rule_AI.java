package secu;

public class Right_hand_rule_AI {
    public static void main(String[] args) {
        String filePath = "D:\\secu_extend\\secu_exten\\src\\secu\\maze.txt";

        int[][] mainMaze = null;

        try {
            mainMaze = MazeLoader.loadFromFile(filePath);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

        for(int i = 0;i< mainMaze.length;i++){
            for(int j = 0;j< mainMaze.length;j++){
                System.out.print(mainMaze[i][j]);
            }
            System.out.println();
        }
        int[][] directions = {
                {1, 0},  // 뒤로
                {0, 1},  // 오른쪽
                {-1, 0}, // 앞
                {0, -1}  // 왼쪽
        };
        int count = 0;
        for(int[] dis : directions){
            System.out.println(count+"번쨰");
            System.out.println(dis[0]);
            System.out.println(dis[1]);
            count++;
        }
    }//-> 모든 방향을 한번씩 순회 모든 방향의 다음위치를 계산하기 위해 현 위치에 각 리스트를 한번씩 더하고 !canMove()함수 돌리면 막히는지 검사도 끝
    

/*
오른손의 법칙 AI

    int[][] directions = {
            {1, 0},  // 뒤로
            {0, 1},  // 오른쪽
            {-1, 0}, // 앞
            {0, -1}  // 왼쪽
    };

    대충 플레이어 다음위치 계산하는 것들
    모든 방향을
    for문 으로 플레이어 인덱스에 상하좌우의 다음위치 계산

        (그리고 여기에 다음위치를 넣어서 계산)
        if(플레이어의 오른쪽이 막혔다?){
          if(플레이어의 앞이 막혔다?){
              if(플레이어의 왼쪽이 막혔다?){
                  뒤로
                  리턴
              }
              왼쪽으로
              리턴
          }
          전진
      }
*/
}
