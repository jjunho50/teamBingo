package com.nhnacademy;

import java.io.IOException;

public class BingoCheck {
    private final String[] BINGO = { "B", "I", "N", "G", "O" };
    String[][] board;

    public BingoCheck(String[][] board) {
        this.board = board;
    }

    public boolean check(String figure) throws IOException {

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

                return true;
            }
        }

        return false;
    }
}
