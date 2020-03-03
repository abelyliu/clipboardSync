package site.abely;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.stream.Collectors;

public class SocketServer implements Runnable {

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(9997);
            System.out.println("START work");
            while (true) {
                Socket accept = serverSocket.accept();
                synchronized (SocketServer.class) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(accept.getInputStream(), "UTF-8"));
                    System.out.println("wait request");
                    String result = bufferedReader
                            .lines().collect(Collectors.joining("\n"));
                    if (result.startsWith("1")) {
                        SocketFileServer.type = 1;
                        System.out.println("type is 1");
                    } else if (result.startsWith("2")) {
                        SocketFileServer.type = 2;
                        System.out.println("type is 2");
                    } else {
                        System.out.println(result);
                    }
                    accept.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
