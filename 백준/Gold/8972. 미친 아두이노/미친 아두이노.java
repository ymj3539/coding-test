import java.util.*;
import java.io.*;
public class Main {
    static BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
    static StringTokenizer tokens;
    static int[] dy = {0, 1, 1, 1, 0, 0, 0, -1, -1, -1};
    static int[] dx = {0, -1, 0, 1, -1, 0, 1, -1, 0, 1};
    static int R, C;
    static char[][] map; // 기본맵
    static Point Jongsu; // 종수의 아두이노 객체
    static List<Point> Arduinos = new ArrayList<>(); // 아두이노를 담을 list
    static Set<Point> Bomb = new HashSet(); // 제거할 아두이노
    static char[][] copymap; // 아두이노 이동 후 맵
    static int[][] checkmap; // 이동 후 아두이노의 인덱스를 담기 위한 맵
    public static void main (String[] args) throws Exception{
        tokens = new StringTokenizer(input.readLine());
        R = Integer.parseInt(tokens.nextToken());
        C = Integer.parseInt(tokens.nextToken());

        map = new char[R][C];

        for(int r=0; r<R; r++){
            String str = input.readLine();
            for(int c=0; c<C; c++){
                map[r][c] = str.charAt(c);
                if(map[r][c] == 'I') {
                    Jongsu = new Point(r, c);
                    map[r][c] = '.';
                }
                else if(map[r][c] == 'R') Arduinos.add(new Point(r, c));
            }
        }

        String str = input.readLine();
        int moveCnt = -1;
        // 이동
        outer : for(int i=0; i<str.length(); i++){
            int dir = str.charAt(i) - '0';

            // 1. 종수 이동
            Jongsu.r += dy[dir];
            Jongsu.c += dx[dir];

            // 2. 종수가 이동한 위치에 아두이노가 있는지 확인
            if(map[Jongsu.r][Jongsu.c] == 'R') {
                moveCnt = i+1;
                break;
            }

            // 3. 미친 아두이노 이동
            // 초기화
            copymap = new char[R][C];
            checkmap = new int[R][C];
            Bomb = new HashSet();
            
            // copymay 채우기
            for(int j=0; j<copymap.length; j++){
                Arrays.fill(copymap[j], '.');
            }

            // 아두이노 진짜루 이동
            for(int j=0; j<Arduinos.size(); j++){
                moveArduino(j);

                // 4. 종수랑 겹치는지 확인
                if(Jongsu.r == Arduinos.get(j).r && Jongsu.c == Arduinos.get(j).c){
                    moveCnt = i+1;
                    break outer;
                }
            }

            // 5. 아두이노끼리 겹치는지 확인
            for(int j=0; j<Arduinos.size(); j++){
                Point ard = Arduinos.get(j);
                if(copymap[ard.r][ard.c] == 'R'){
                    Bomb.add(Arduinos.get(checkmap[ard.r][ard.c])); // 이미 존재하던 아두이노를 Bomb에 담음
                    Bomb.add(Arduinos.get(j)); // 현재 아두이노도 Bomb에 담음
                }
                else {
                    copymap[ard.r][ard.c] = 'R';
                    checkmap[ard.r][ard.c] = j; // 아두이노 index 저장
                }
            }

            // 6. 아두이노 파괴
            if(Bomb.size() != 0){
                for(Point ardu : Bomb){
                    copymap[ardu.r][ardu.c] = '.';
                    Arduinos.remove(ardu);
                }
            }

            for(int r=0; r<R; r++){
                map[r] = copymap[r].clone();
            }
            
        }

        if(moveCnt == -1){
            StringBuilder sb = new StringBuilder();
            // 맵 출력
            map[Jongsu.r][Jongsu.c] = 'I';
            for(int r=0; r<R; r++){
                for(int c=0; c<C; c++) {
                    sb.append(map[r][c]);
                }
                sb.append("\n");
            }
            System.out.println(sb);

        } else{
            System.out.println("kraj "+moveCnt);
        }
    }

    static void moveArduino(int idx){
        Point ard = Arduinos.get(idx);

        int min_dist = Integer.MAX_VALUE;
        int nr = 0;
        int nc = 0;
        for(int i=1; i<=9; i++){
            if(i==5) continue;
            int dr = ard.r + dy[i];
            int dc = ard.c + dx[i];
            
            // 거리 계산
            int dist = Math.abs(Jongsu.r - dr) + Math.abs(Jongsu.c - dc);
            if(min_dist > dist){
                nr = dr;
                nc = dc;
                min_dist = dist;
            }
        }

        // 아두이노 이동
        ard.r = nr;
        ard.c = nc;
    }

    static class Point{
        int r, c;
        public Point(int r, int c){
            this.r = r;
            this.c = c;
        }
    }
}