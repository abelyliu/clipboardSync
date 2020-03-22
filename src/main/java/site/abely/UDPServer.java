package site.abely;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPServer {
    public static void run() {
        new Thread(() -> {
            int port = 9997;//开启监听的端口
            DatagramSocket ds = null;
            DatagramPacket dp = null;
            while (true) {
                byte[] buf = new byte[1024];//存储发来的消息
                StringBuffer sbuf = new StringBuffer();
                try {
                    System.out.println();
                    //绑定端口的
                    ds = new DatagramSocket(port);
                    dp = new DatagramPacket(buf, buf.length);
                    System.out.println("监听广播端口打开：");
                    ds.receive(dp);
                    ds.close();
                    InetAddress address = dp.getAddress();
                    if (!address.getHostAddress().equals(InetAddress.getLocalHost().getHostAddress())) {
                        int i;
                        for (i = 0; i < 1024; i++) {
                            if (buf[i] == 0) {
                                break;
                            }
                            sbuf.append((char) buf[i]);
                        }
                        if (sbuf.toString().equals(UDPClient.message)) {
                            SendService.host = address.getHostAddress();
                        }
                    }

                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
