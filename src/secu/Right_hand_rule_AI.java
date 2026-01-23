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




    }

/*

*/
}
