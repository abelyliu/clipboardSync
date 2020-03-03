package site.abely;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.File;
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
                        String substring = result.substring(1);
                        File file = new File(substring);
                        String name = file.getName();
                        SocketFileServer.filename = name;
                        System.out.println("file name is " + name);
                        System.out.println("type is 1");
                    } else if (result.startsWith("2")) {
                        SocketFileServer.type = 2;
                        System.out.println("type is 2");
                    } else {
                        StringSelection stringSelection = new StringSelection(result);
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, stringSelection);
                        System.out.println("copy finish");
                    }
                    accept.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
