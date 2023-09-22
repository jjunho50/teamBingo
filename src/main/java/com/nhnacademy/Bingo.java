package com.nhnacademy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

class Bingo extends Thread {
    private final String[] BINGO = { "B", "I", "N", "G", "O" };
    static List<Bingo> serverList = new LinkedList<>();
    static String[] figures = { "O", "X" };

    static boolean firstSetting = true;
    static int count = 0;
    static int order = 0;

    boolean[][] changed = new boolean[5][5];
    String[][] board = new String[5][5];
    String userName;
    String figure;
    Socket socket;
    BufferedWriter out;

    public Bingo(Socket socket) {
        this.socket = socket;
        this.userName = "" + (order++ % 2); // 유저 이름은 0, 1 둘 중 하나다.
        serverList.add(this);
    }

    public void send(String message) throws IOException {
        out.write(message);
        out.flush();
    }

    public String getUserName() {
        return userName;
    }

    public void setFigure(String figure) {
        this.figure = figure;
    }

    public String getFigure() {
        return figure;
    }

    // 유저 이름으로 찾기
    public Bingo getServer(String userName) {
        for (Bingo server : serverList) {
            if (server.getUserName().equals(userName)) {
                return server;
            }
        }

        throw new NoSuchElementException(userName + "은 존재하지 않는 이름입니다.\n");
    }

    // 자신의 보드 출력
    public String showBoard() {
        String s = "\n==============================\n";
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                String cellValue = String.valueOf(board[i][j]);
                // 셀의 값이 한 자릿수일 때 앞에 공백 추가
                if (cellValue.length() == 1) {
                    cellValue = " " + cellValue;
                }
                // 셀의 값을 중앙 정렬하기 위해 공백 추가
                int cellLength = cellValue.length();
                int padding = (5 - cellLength) / 2;
                for (int k = 0; k < padding; k++) {
                    s += " ";
                }
                s += "[" + cellValue + "]";
                for (int k = 0; k < 5 - cellLength - padding; k++) {
                    s += " ";
                }
            }
            s += "\n";
        }
        s += "==============================\n";

        return s;
    }

    public int getIndex(int num) {
        String tmp = String.valueOf(num);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (board[i][j].equals(tmp)) {
                    return i * 5 + j;
                }
            }
        }
        throw new IllegalArgumentException("잘못된 값을 입력했습니다.");
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
            this.out = out; // Why?

            while (!Thread.currentThread().isInterrupted()) {
                if (serverList.size() == 1) {
                    // send("다른 플레이어의 입장을 기다려 주세요.\n");
                } else if (serverList.size() == 2) {

                    // 선공, 후공이 정해진 이후로는 출력되지 않습니다.
                    if (firstSetting) {
                        send("게임을 시작합니다. 빙고판은 랜덤으로 생성됩니다.");

                        for (int i = 0; i < 5; i++) {
                            for (int j = 0; j < 5; j++) {
                                board[i][j] = String.valueOf((i * 5 + j) + 1);
                            }
                        }

                        // 1~25의 숫자 섞기
                        Random rd = new Random();
                        for (int i = 0; i < 5; i++) {
                            for (int j = 0; j < 5; j++) {
                                int a = rd.nextInt(5);
                                int b = rd.nextInt(5);

                                String tmp;
                                tmp = board[i][j];
                                board[i][j] = board[a][b];
                                board[a][b] = tmp;
                            }
                        }

                        send(showBoard());

                        setFigure(figures[Integer.parseInt(userName)]);

                        String message = (figure.equals("O")) ? "선공입니다! (O)" : "후공입니다! (X)";
                        send(message + "\n");

                        firstSetting = false;
                    }

                    if (count % 2 == 0) {
                        playGameInOrder(in, getServer("0"), getServer("1"));
                    } else {
                        playGameInOrder(in, getServer("1"), getServer("0"));
                    }
                }
            }
        } catch (IOException e) {
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playGameInOrder(BufferedReader in, Bingo first, Bingo second) throws IOException {
        // 빙고가 완성되었다면
        if (check(figure)) {
            for (Bingo server : serverList) {
                if (this.equals(server))
                    server.send("\n당신의 승리입니다 축하합니다!!!!\n");
                else
                    server.send("\n당신의 패배입니다 ㅠㅠㅠ 다음 기회를 노려보세요.\n");
            }
            System.exit(1);
        }

        // first = 선공
        if (this.equals(first)) {
            for (Bingo server : serverList) {
                if (server.equals(first))
                    server.send("당신 차례입니다! 원하는 위치의 숫자를 입력하세요.(" + first.getFigure() + ") : "); // 선공
                else {
                    server.send("한 턴 쉬세요~(입력 금지!!! 버그 나요)\n"); // 후공
                }
            }

            int index = -1;
            boolean validInput = false;
            boolean printOnlyOne = false; // ErrorMessage는 한번만 띄운다 (사유 : 꼴보기 싫음)

            // 유효 입력 판별
            while (!validInput) {
                try {
                    index = Integer.parseInt(in.readLine()); // 바꿀 숫자
                    int location = getIndex(index); // 입력된 위치의 숫자

                    // 유효한 입력인지 확인하고 중복된 입력이 아닌 경우에만 진행
                    if (index >= 1 && index <= 25 && !changed[location / 5][location % 5]) {
                        validInput = true;

                        int secondLocation = second.getIndex(index);
                        changed[location / 5][location % 5] = true; // 해당 셀은 변경됨.
                        second.changed[secondLocation / 5][secondLocation % 5] = true; // 다른 플레이어의 셀도 변경

                        board[location / 5][location % 5] = figure; // 내 보드 변경
                        second.board[secondLocation / 5][secondLocation % 5] = figure; // 다른 플레이어 보드 변경

                    } else {
                        if (this.equals(first))
                            send("잘못된 입력입니다. 다시 입력하세요: ");
                    }
                } catch (NumberFormatException e) {
                    // 숫자로 변환할 수 없는 입력 처리
                    if (this.equals(first) && !printOnlyOne) {
                        send("숫자를 입력하세요: ");
                        printOnlyOne = true;
                    }
                }

            }

            // 바뀐 보드 보여주기
            for (Bingo server : serverList) {
                if (server.equals(first))
                    send(showBoard());
                else {
                    server.send(server.showBoard());
                }
            }

            count++;

            if (count == 25) {
                draw();
            }
        }
    }

    private boolean check(String figure) throws IOException {

        // 가로 ("BINGO로 변경")
        for (int i = 0; i < 5; i++) {
            int cnt = 0;
            for (int j = 0; j < 5; j++) {
                if (board[i][j].equals(figure)) {
                    cnt++;

                    if (cnt == 5) {
                        for (int k = 0; k < 5; k++) {
                            board[i][k] = BINGO[k]; // BINGO!!
                        }

                        Bingo winner = this;
                        for (Bingo server : serverList) {
                            server.send(winner.showBoard());
                        }
                        return true;
                    }
                }

            }
        }

        // 세로
        for (int j = 0; j < 5; j++) {
            int cnt = 0;
            for (int i = 0; i < 5; i++) {
                if (board[i][j].equals(figure))
                    cnt++;
            }

            if (cnt == 5) {
                for (int k = 0; k < 5; k++) {
                    board[k][j] = BINGO[k]; // BINGO!!
                }

                Bingo winner = this;
                for (Bingo server : serverList) {
                    server.send(winner.showBoard());
                }
                return true;
            }
        }
        // 좌 대각선
        int cnt = 0;
        for (int i = 0; i < 5; i++) {
            if (board[i][i].equals(figure))
                cnt++;

            if (cnt == 5) {
                for (int j = 0; j < 5; j++) {
                    board[j][j] = BINGO[j];
                }

                Bingo winner = this;
                for (Bingo server : serverList) {
                    server.send(winner.showBoard());
                }
                return true;
            }
        }

        // 우 대각선
        cnt = 0;
        for (int i = 0, j = 4; i < 5; i++, j--) {

            if (board[i][j].equals(figure))
                cnt++;

            if (cnt == 5) {
                for (int k = 0, l = 4; k < 5; k++, l--) {
                    board[k][l] = BINGO[k]; // BINGO!!
                }

                Bingo winner = this;
                for (Bingo server : serverList) {
                    server.send(winner.showBoard());
                }
                return true;
            }
        }

        return false;
    }

    // 무승부 선언 (게임 종료)
    private void draw() throws IOException {
        for (Bingo server : serverList) {
            server.send("비겼습니다. 게임을 종료합니다!\n");
        }
        System.exit(1);
    }

    public static void main(String[] args) {
        int port = 1234;
        List<Bingo> serverList = new LinkedList<>();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (!Thread.currentThread().isInterrupted()) {
                Socket socket = serverSocket.accept();

                // 두 명의 참가자만 허용
                if (serverList.size() < 2) {
                    Bingo server = new Bingo(socket);
                    server.start();
                    serverList.add(server);
                } else {
                    // 이미 두 명의 참가자가 있는 경우, 새로운 연결을 거부
                    socket.close();
                }
            }
        } catch (Exception e) {
        }
    }
}