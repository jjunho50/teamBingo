package com.nhnacademy;

import com.nhnacademy.exceptions.NotChangedException;
import com.nhnacademy.exceptions.NotInBoundsException;

public class Bingo {
    private final String[] BINGO = { "B", "I", "N", "G", "O" };
    String[][] array;
    String result;

    public Bingo(int n) {
        this.array = new String[n][n];
    }

    // 빙고판 세팅
    public void setting(String a) {
        String[] numbers = a.split(",");
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array.length; j++) {
                array[i][j] = numbers[array.length * i + j].trim();
            }
        }
    }

    // 나의 차례일 때의 처리
    public void inputO(String a) {
        boolean isChanged = false;
        int x = Integer.parseInt(a);
        if (x > 100 || x < 1) {
            throw new NotInBoundsException();
        }
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                if (array[i][j].equals(a)) {
                    array[i][j] = "[" + a + "]";
                    isChanged = true;
                }
            }
        }
        if (!isChanged) {
            throw new NotChangedException();
        }
    }

    // 상대의 차례일 때의 처리
    public void inputX(String a) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                if (array[i][j].equals(a)) {
                    array[i][j] = "XX";
                }
            }
        }
    }
}