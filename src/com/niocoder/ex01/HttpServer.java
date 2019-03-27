package com.niocoder.ex01;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

    /**
     * WEB_ROOT 是HTML和其他文件所在的目录
     * 即工作目录下的webRoot目录
     * 它是java通过调用文件系统命令得到的
     */
    public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "webroot";

    // 关闭容器
    private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";

    // 表示关闭的命令
    private boolean shutdown = false;

    public static void main(String[] args) {
        HttpServer server = new HttpServer();
        server.await();
    }

    public void await() {
        ServerSocket serverSocket = null;
        int port = 8080;

        try {
            serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // 循环等到请求连接
        while (!shutdown) {
            Socket socket = null;
            InputStream input = null;
            OutputStream output = null;

            try {
                socket = serverSocket.accept();
                input = socket.getInputStream();
                output = socket.getOutputStream();

                // 创建Request 对象接收 input 流并解析
                Request request = new Request(input);
                request.parse();

                // 创建Response 对象
                Response response = new Response(output);
                response.setRequest(request);
                response.sendStaticResource();

                // 关闭socket
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
    }
}
