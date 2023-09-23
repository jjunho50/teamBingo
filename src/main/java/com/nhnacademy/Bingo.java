package com.nhnacademy;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

public class Bingo extends Thread {

    static List<Bingo> serverList = new LinkedList<>();
    static String[] figures = { "O", "X" };

    static int count = 0;
    static int order = 0;

    boolean[][] changed = new boolean[5][5];
    String[][] board = new String[5][5];
    BingoBoard bingoBoard;
    BingoCheck bingoCheck;

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

    // 유저 이름으로 찾기
    public Bingo getServer(String userName) {
        for (Bingo server : serverList) {
            if (server.getUserName().equals(userName)) {
                return server;
            }
        }

        throw new NoSuchElementException(userName + "은 존재하지 않는 이름입니다.\n");
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
            this.out = out;

            synchronized (serverList) {
                if (serverList.size() < 2) {
                    send("다른 플레이어의 접속을 기다리는중...\n");
                    serverList.wait();
                }

                if (serverList.size() == 2) {
                    serverList.notifyAll();
                }
            }

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

            bingoBoard = new BingoBoard(board); // 보드 클래스

            send(bingoBoard.showBoard());
            setFigure(figures[Integer.parseInt(userName)]);

            String message = (figure.equals("O")) ? "선공입니다! (O)" : "후공입니다! (X)";
            send(message + "\n");

            while (!Thread.currentThread().isInterrupted()) {
                if (count % 2 == 0) {
                    playGameInOrder(in, getServer("0"), getServer("1"));
                } else {
                    playGameInOrder(in, getServer("1"), getServer("0"));
                }
            }
        } catch (IOException e) {
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playGameInOrder(BufferedReader in, Bingo first, Bingo second) throws IOException {
        // 빙고가 완성되었다면
        bingoCheck = new BingoCheck(board);

        if (bingoCheck.check(figure)) {
            for (Bingo server : serverList) {
                if (this.equals(server)) {
                    getWinner();
                    server.send("\n당신의 승리입니다 축하합니다!!!!\n");
                }

                else {
                    getWinner();
                    server.send("\n당신의 패배입니다 ㅠㅠㅠ 다음 기회를 노려보세요.\n");
                }

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

                    // 입력값의 범위를 확인
                    if (index >= 1 && index <= 25) {
                        int location = getIndex(index); // 입력된 위치의 숫자

                        // 유효한 입력인지 확인하고 중복된 입력이 아닌 경우에만 진행
                        if (!changed[location / 5][location % 5]) {
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
                    } else {
                        if (this.equals(first))
                            send("1에서 25 사이의 숫자를 입력하세요: ");
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
                    send(bingoBoard.showBoard());
                else {
                    server.send(server.bingoBoard.showBoard());
                }
            }

            count++;

            if (count == 25) {
                draw();
            }
        }
    }

    public void getWinner() throws IOException {
        Bingo winner = this;
        for (Bingo server : serverList) {
            server.send(winner.bingoBoard.showBoard());
        }
    }

    // 무승부 선언 (게임 종료)
    private void draw() throws IOException {
        for (Bingo server : serverList) {
            server.send("비겼습니다. 게임을 종료합니다!\n");
        }
        System.exit(1);
    }
}
