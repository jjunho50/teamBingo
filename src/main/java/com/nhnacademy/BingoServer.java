package com.nhnacademy;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class BingoServer {
    public static void main(String[] args) {
        int port = 1234;
        List<Bingo> serverCounter = new LinkedList<>();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (!Thread.currentThread().isInterrupted()) {
                Socket socket = serverSocket.accept();

                // 두 명의 참가자만 허용
                if (serverCounter.size() < 50) {
                    Bingo server = new Bingo(socket);
                    server.start();
                    serverCounter.add(server);
                } else {
                    // 이미 두 명의 참가자가 있는 경우, 새로운 연결을 거부
                    socket.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}