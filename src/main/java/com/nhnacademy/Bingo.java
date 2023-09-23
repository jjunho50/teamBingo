package com.nhnacademy;

import com.nhnacademy.exceptions.NotChangedException;
import com.nhnacademy.exceptions.NotEnoughException;
import com.nhnacademy.exceptions.NotInBoundsException;

public class Bingo {
    private final String[] BINGO = { "B", "I", "N", "G", "O" };
    String[][] array;
    String result;
    int size;

    public Bingo(int n) {
        this.array = new String[n][n];
        this.size = n;
    }

    // 빙고판 세팅
    // NumberFormatException - 숫자가 아닙니다.
    // NotInBoundsException - 1부터 size * size까지의 정수를 입력해주세요.
    // NotEnoughException - 숫자가 모자랍니다.
    public void setting(String a) {
        String[] numbers = a.split(",");
        if (numbers.length > size * size) {
            throw new NotEnoughException();
        }
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int x = Integer.parseInt(numbers[size * i + j].trim());
                if (x > size * size || x <= 0) {
                    throw new NotInBoundsException();
                }
                array[i][j] = numbers[size * i + j].trim();
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
        if (x > size * size || x <= 0) {
            throw new NotInBoundsException();
        }
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
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
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (array[i][j].equals(a)) {
                    array[i][j] = "XX";
                }
            }
        }
    }

    // 게임 종료 검사
    private boolean check(String[][] array) {
        // 가로
        for (int i = 0; i < size; i++) {
            int count = 0;
            for (int j = 0; j < size; j++) {
                if (array[i][j].charAt(0) == '[') {
                    count++;
                }
            }
            if (count == size) {
                for (int j = 0; j < size; j++) {
                    array[i][j] = BINGO[j];
                }
                return true;
            }
        }

        // 세로
        for (int i = 0; i < size; i++) {
            int count = 0;
            for (int j = 0; j < size; j++) {
                if (array[j][i].charAt(0) == '[') {
                    count++;
                }
            }
            if (count == size) {
                for (int j = 0; j < size; j++) {
                    array[j][i] = BINGO[j];
                }
                return true;
            }
        }

        // 좌 대각선
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (array[i][i].charAt(0) == '[') {
                count++;
            }
            if (count == size) {
                for (int j = 0; j < size; j++) {
                    array[j][j] = BINGO[j];
                }
                return true;
            }
        }
        // 우 대각선
        count = 0;
        for (int i = 0, j = size - 1; i < size; i++, j--) {
            if (array[i][j].charAt(0) == '[') {
                count++;
            }
            if (count == size) {
                for (int k = 0, l = size - 1; k < size; k++, l--) {
                    array[k][l] = BINGO[k];
                }
                return true;
            }
        }
        return false;
    }

    // 빙고판 출력
    public String toString() {
        String string = "\n";
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                string += array[i][j] + "\t";
            }
            string += "\n";
        }
        return string;
    }

    public static void main(String[] args) {
        Bingo bingo = new Bingo(5);
        System.out.println(bingo);
        bingo.setting("1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25");
        System.out.println(bingo);

        bingo.inputO("3");
        System.out.println(bingo);

        bingo.inputX("4");
        System.out.println(bingo);

        bingo.inputO("4");
        System.out.println(bingo);

    }
}