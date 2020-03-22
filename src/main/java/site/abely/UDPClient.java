package site.abely;

import java.io.IOException;
import java.net.*;

public class UDPClient {
    public static String message1 = "clip 1";//用于发送的字符串
    public static String message2 = "clip 2";//用于发送的字符串

    public static void run(String message) {
        // 广播的实现 :由客户端发出广播，服务器端接收
        String host = "255.255.255.255";//广播地址
        int port = 9997;//广播的目的端口
        try {
            InetAddress adds = InetAddress.getByName(host);
            DatagramSocket ds = new DatagramSocket();
            DatagramPacket dp = new DatagramPacket(message.getBytes(),
                    message.length(), adds, port);
            ds.send(dp);
            ds.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
