package secu;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class ReplayLoader {
   private static int Csize;
   private static char[] prMove;
   private static char[] aiMove;
   private static int[][] maze;


    public static void loadReplay(String fileName) throws IOException {
        try {
            // ✅ 1. 파일 읽기 (한 줄로 저장되어 있다고 가정)
            String data = Files.readString(Paths.get(fileName));

            if(data.isEmpty()) throw new IOException("파일이 비어있습니다.");

            // ✅ 2. 파싱
            String[] parts = data.split("e");

            // 파싱 테스트
            for(String de : parts){
                System.out.println(de);
            }

            // ✅ 3. 크기
            int size = Integer.parseInt(parts[0]);
            Csize = size;


            // ✅ 4. 미로 배열
            int[][] mazeArray = new int[size][size];
            String mazeStr = parts[1];
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    mazeArray[i][j] = mazeStr.charAt(i * size + j) - '0';
                }
            }
            maze = mazeArray;

            // ✅ 5. 이동 배열
            String movesStr = parts[2].substring(0, parts[2].length());
            char[] movesArrayA = parts[2].toCharArray();
            char[] movesArrayP = parts[3].toCharArray();
            aiMove = movesArrayA;
            prMove = movesArrayP;


            System.out.println("이동배열");
            for (char mo : movesArrayA){
                System.out.print(mo+" ");
            }
            System.out.println();
            for (char mo : movesArrayP){
                System.out.print(mo+" ");
            }


            // ✅ 6. 확인 출력
            System.out.println("\n 크기: " + size);
            System.out.println("미로 배열:");
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    System.out.print(mazeArray[i][j]);
                }
                System.out.println();
            }



        } catch (IOException e) {
            System.out.println("파일 읽기 실패: " + e.getMessage());
        }
    }
    public static void main(String[] args) {
        try {
            loadReplay("/Users/user/Desktop/secu_project/src/test/game_save2.txt");
        } catch (Exception e) {
            System.out.println(e.getMessage()+"문제 발생");
        }

        for (char mo : prMove){
            switch (mo){
                case '↑' :
                    System.out.print(0 + " ");
                    break;
                case '→' :
                    System.out.print(1 + " ");
                    break;
                case '←' :
                    System.out.print(2 + " ");
                    break;
                case '↓' :
                    System.out.print(3 + " ");
                    break;
            }
        }
        System.out.println();
        for(char mo : prMove){
            System.out.print(mo+" ");
        }

    }
}