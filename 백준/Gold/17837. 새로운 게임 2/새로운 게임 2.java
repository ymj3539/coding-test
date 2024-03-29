import java.util.*;
import java.io.*;
public class Main {
    static BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
    static StringTokenizer tokens;
    static int N, K;
    static int[][] map; // 색깔 map
    static Piece[] pieces; // 말 담을 배열
    static Piece[][] Piece_map; // 현재 좌표에서 가장 바닥에 해당되는 말
    static int[] dy = {0, 0, 0, -1, 1}; // 0 우 좌 상 하
    static int[] dx = {0, 1, -1, 0, 0};

    public static void main(String[] args) throws Exception{
        tokens = new StringTokenizer(input.readLine());
        N = Integer.parseInt(tokens.nextToken());
        K = Integer.parseInt(tokens.nextToken());

        map = new int[N+1][N+1];
        Piece_map = new Piece[N+1][N+1];

        for(int r=1; r<=N; r++){
            tokens = new StringTokenizer(input.readLine());
            for(int c=1; c<=N; c++){
                map[r][c] = Integer.parseInt(tokens.nextToken());
            }
        }

        pieces = new Piece[K+1];

        for(int i=1; i<=K; i++){
            tokens = new StringTokenizer(input.readLine());
            int r = Integer.parseInt(tokens.nextToken());
            int c = Integer.parseInt(tokens.nextToken());
            int dir = Integer.parseInt(tokens.nextToken());

            pieces[i] = new Piece (r, c, dir, null, null, i);
            Piece_map[r][c] = pieces[i];
        }



        // 턴 반복
        int turn = 1;
        outer : while(turn <= 1000){
            // 1 ~ K 까지의 말 이동
            for(int i=1; i<=K; i++){
                // 옮겨야하는 말
                Piece cur = pieces[i];

                // 이동하려는 칸 확인
                int dr = cur.r + dy[cur.dir];
                int dc = cur.c + dx[cur.dir];

                // 범위 확인
                if(!isIn(dr, dc)){
                    // 파란색처럼 작동
                    if(!blue(i)) break outer;
                }else {
                    // 흰색(1)일 때
                    if(map[dr][dc] == 0){
                        // 흰색일 때 메소드
                        if(!white(i, dr, dc)) break outer;
                    }
                    // 빨강(1)일 때
                    else if(map[dr][dc] == 1){
                        // 빨강일 때 메소드
                        if(!red(i, dr, dc)) break outer;

                    }
                    // 파랑(2)일 때
                    else if(map[dr][dc] == 2){
                        if(!blue(i)) break outer;
                    }

                }
            }
            turn++;
        }

        System.out.println(turn > 1000 ? -1 : turn);
    }

    static boolean white(int i, int dr, int dc){
        Piece cur = pieces[i];

        // cur의 bottom이 있으면 연결 끊어주기
        if(cur.bottom != null){
            cur.bottom.top = null;
            cur.bottom = null;
        }

        // 현재 말이 제일 밑바닥이면 비우고 이동
        if(Piece_map[cur.r][cur.c] == cur){
            Piece_map[cur.r][cur.c] = null;
        }

        // 그 칸으로 이동
        cur.move(dr, dc);

        // 해당 칸에 말이 있을 때
        if(Piece_map[dr][dc] != null){
            // 해당 칸의 제일 위의 말 찾기
            Piece map_bottom = Piece_map[dr][dc];
            while(map_bottom.top != null){
                Piece map_top = map_bottom.top;
                map_bottom = map_top;
            }

            // 제일 위의 말과 연결해주기
            map_bottom.top = cur;
            cur.bottom = map_bottom;
        }
        // 말이 없을 때
        else {
            // 현재 말이 밑바닥
            Piece_map[dr][dc] = cur;
        }

        // 쌓인 말 갯수 체크
        if(canGo(dr, dc)){
            return true;
        }
        
        else return false;
    }

    static boolean red(int i, int dr, int dc){
        if(!white(i, dr, dc)) return false;

        // 순서 바꾸기
        Piece cur = pieces[i];

        // 현재 말 위에 순서바꿔줘야 될게 있을 때
        if(cur.top != null){
            Stack<Piece> stack = new Stack<>();

            Piece tmp = cur.bottom; // 현재 말 아래에 있는 말 따로 저장

            // 현재 말 위에 있는 말들 stack에 넣어주기
            while(cur != null){
                stack.push(cur);
                cur = cur.top;
            }

            Piece now = stack.pop();
            
            if(tmp != null){
                // 아까 따로 저장했던 말과 연결
                tmp.top = now;
            }else {
                // 밑에 다른 말 없으면 얘가 밑바닥
                Piece_map[dr][dc] = now;
            }
            
            now.bottom = tmp;

            // stack에서 하나씩 뽑으면서 순서 반대로 연결
            while(!stack.isEmpty()){
                Piece next = stack.pop();
                now.top = next;
                next.bottom = now;
                now = next;
            }

            // 맨 위의 말의 top은 null
            now.top = null;
        }
         return true;
    }

    static boolean blue(int i){
        Piece cur = pieces[i];
        // 이동 방향 반대로
        cur.changeDir();

        // 이동 하려는 칸 확인
        int br = cur.r + dy[cur.dir];
        int bc = cur.c + dx[cur.dir];

        // 이동하려는 칸이 범위 밖이나 blue면 이동X
        if(!isIn(br, bc) || map[br][bc] == 2){
            return true;
        }else if(map[br][bc] == 0){
            if(white(i, br, bc)) return true;
            else return false;
        }else {
            if(red(i, br, bc)) return true;
            else return false;
        }
    }


    // 쌓인 말이 4개 미만인지 체크
    static boolean canGo(int r, int c){
        int cnt = 0;
        Piece cur = Piece_map[r][c];

        while(cur != null && cnt < 4){
            cnt++;
            cur = cur.top;
        }

        if(cnt >= 4) return false; // 4개 미만이면 종료
        return true;
    }

    static boolean isIn(int r, int c){
        return (r>=1 && c>=1 && r<=N && c<=N);
    }

    // 말 객체
    static class Piece{
        // 좌표, 방향, 말 번호
        int r, c, dir, num;
        Piece bottom, top;

        public Piece(int r, int c, int dir, Piece bottom, Piece top, int num){
            this.r = r;
            this.c = c;
            this.dir = dir;
            this.bottom = bottom;
            this.top = top;
            this.num = num;
        }

        // 말 좌표 이동 시키기
        public void move(int dr, int dc){
            this.r = dr;
            this.c = dc;

            // 위에 있는 말도 같이 이동
            Piece piece = this.top;
            while(piece != null){
                piece.r = dr;
                piece.c = dc;

                piece = piece.top;
            }
        }

        // 말 방향 전환
        public void changeDir(){
            if(this.dir == 1){
                this.dir = 2;
            }else if(this.dir == 2){
                this.dir = 1;
            }else if(this.dir == 3){
                this.dir = 4;
            }else if(this.dir == 4){
                this.dir = 3;
            }
        }

    }
}