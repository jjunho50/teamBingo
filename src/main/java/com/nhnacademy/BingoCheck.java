package com.nhnacademy;

import java.io.IOException;

public class BingoCheck {
    private final String[] BINGO = { "B", "I", "N", "G", "O" };
    String[][] board;
    int size;

    public BingoCheck(String[][] board) {
        this.board = board;
        size = board.length;
    }

    public boolean check(String figure) throws IOException {

        // 가로 ("BINGO로 변경")
        for (int i = 0; i < size; i++) {
            int cnt = 0;
            for (int j = 0; j < size; j++) {
                if (board[i][j].equals(figure)) {
                    cnt++;

                    if (cnt == size) {
                        for (int k = 0; k < size; k++) {
                            if (k >= 5) {
                                board[i][k] = "!";
                            } else {
                                board[i][k] = BINGO[k]; // BINGO!!
                            }
                        }
                        return true;
                    }
                }
            }
        }

        // 세로
        for (int j = 0; j < size; j++) {
            int cnt = 0;
            for (int i = 0; i < size; i++) {
                if (board[i][j].equals(figure))
                    cnt++;
            }

            if (cnt == size) {
                for (int k = 0; k < size; k++) {
                    if (k >= 5) {
                        board[k][j] = "!";
                    }
                    board[k][j] = BINGO[k]; // BINGO!!
                }

                return true;
            }
        }
        // 좌 대각선
        int cnt = 0;
        for (int i = 0; i < size; i++) {
            if (board[i][i].equals(figure))
                cnt++;

            if (cnt == size) {
                for (int j = 0; j < size; j++) {
                    if (j >= 5) {
                        board[j][j] = "!";
                    }
                    board[j][j] = BINGO[j];
                }

                return true;
            }
        }

        // 우 대각선
        cnt = 0;
        for (int i = 0, j = size - 1; i < size; i++, j--) {

            if (board[i][j].equals(figure))
                cnt++;

            if (cnt == 5) {
                for (int k = 0, l = size - 1; k < size; k++, l--) {
                    if (k >= 5) {
                        board[k][l] = "!";
                    }
                    board[k][l] = BINGO[k]; // BINGO!!
                }

                return true;
            }
        }

        return false;
    }
}
