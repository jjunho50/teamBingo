package com.nhnacademy;

public class BingoBoard {
    String[][] board;

    public BingoBoard(String[][] board) {
        this.board = board;
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
}