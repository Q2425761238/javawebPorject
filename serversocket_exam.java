package com.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpClientReceive implements Runnable {

    private ServerSocket serverSocket;

    private boolean isRunning;

    public HttpClientReceive(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {

        HttpClientReceive httpClientReceive = new HttpClientReceive(80);

        httpClientReceive.start();

    }

    public void stop() {
        this.isRunning = false;
    }

    public void start() {
        this.isRunning = true;
        new Thread(this).start();
    }

    @Override
    public void run() {
        Socket socket = null;
        String temp = null;
        while (isRunning) {
            try {
                socket = serverSocket.accept();

                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line = null;

                if((line = bufferedReader.readLine()) != null && !line.equals("")) {
                    if (temp == null) {
                        temp = line;
                    }
                    stringBuilder.append(line).append("/n");
                }

                if (!"/favicon.ico".equals(temp.split(" ")[1])) {

                    String[] temps = temp.split(" ");
                    //获取第一个参数
                    String param1 = temps[1].substring(temps[1].lastIndexOf('=') + 1).trim();
                    //截取运算符的类型
                    String type = temps[1].substring(temps[1].lastIndexOf('/') + 1, temps[1].indexOf('?'));
                    //获取第二个参数
                    String param2 = temps[1].substring(temps[1].indexOf('=') + 1, temps[1].lastIndexOf('&')).trim();

                    int result = caculate(type, param1, param2);

                    System.out.println("----------" + result);

                    PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true); //自动刷新缓存

                    doEcho(printWriter, result + "");

                    printWriter.close();
                }
                socket.close();

                temp = null;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private int caculate(String type, String param1, String param2) {
        int result = 0;
        if ("mutl".equals(type)) {
            result = Integer.parseInt(param1) * Integer.parseInt(param2);
        }
        if ("add".equals(type)) {
            result = Integer.parseInt(param1) + Integer.parseInt(param2);
        }
        if ("sub".equals(type)) {
            result = Integer.parseInt(param1) - Integer.parseInt(param2);
        }
        if ("divide".equals(type)) {
            if (!"0".equals(param2))
                result = Integer.parseInt(param1) / Integer.parseInt(param2);
            else {
                throw new RuntimeException("param is mistake");
            }
        }
        return result;
    }

    private void doEcho(PrintWriter printWriter, String record) {
        printWriter.write(record);
    }

}
