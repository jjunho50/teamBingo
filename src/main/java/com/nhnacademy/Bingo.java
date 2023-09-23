package com.nhnacademy;

import com.nhnacademy.exceptions.NotChangedException;
import com.nhnacademy.exceptions.NotEnoughException;
import com.nhnacademy.exceptions.NotInBoundsException;

public class Bingo {
    private final String[] BINGO = { "B", "I", "N", "G", "O" };
    String[][] array;
    String result;

    public Bingo(int n) {
        this.array = new String[n][n];
    }

    // 빙고판 세팅
    // NumberFormatException - 숫자가 아닙니다.
    // NotInBoundsException - 1부터 99까지의 정수를 입력해주세요.
    // NotEnoughException - 숫자가 모자랍니다.
    public void setting(String a) {
        String[] numbers = a.split(",");
        if (numbers.length > array.length * array.length) {
            throw new NotEnoughException();
        }
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array.length; j++) {
                int x = Integer.parseInt(numbers[array.length * i + j].trim());
                if (x > 100 || x <= 0) {
                    throw new NotInBoundsException();
                }
                array[i][j] = numbers[array.length * i + j].trim();
            }
        }
    }

    // 나의 차례일 때의 처리
    // NumberFormatException
    // NotInBoundsException
    // NotChangedException - 숫자가 빙고판에 없습니다.
    public void inputO(String a) {
        boolean isChanged = false;
        int x = Integer.parseInt(a);
        if (x > 100 || x <= 0) {
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

    // 빙고판 출력
    public String toString() {
        String string = "";
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                string += array[i][j] + "/t";
            }
            string += "\n";
        }
        return string;
    }

}