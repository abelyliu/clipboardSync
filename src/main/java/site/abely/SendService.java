package site.abely;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

public class SendService {


    public void send(int type, String context, byte[] files, BufferedImage image) {

        SocketAddress socketAddress = new InetSocketAddress("127.0.0.1", 9997);
        SocketAddress socketAddress2 = new InetSocketAddress("127.0.0.1", 9996);
        Socket socket = new Socket();
        try {
            socket.connect(socketAddress);
            OutputStream outputStream = socket.getOutputStream();
            StringBuilder sb = new StringBuilder().append(type).append(context);
            outputStream.write(sb.toString().getBytes());
            outputStream.flush();
            socket.close();
            Socket socket2 = new Socket();
            if (files != null) {
                socket2.connect(socketAddress2);
                OutputStream outputStream2 = socket.getOutputStream();
                outputStream2.write(files);
                outputStream2.flush();
            }
            if (image != null) {
                socket2.connect(socketAddress2);
                OutputStream outputStream2 = socket2.getOutputStream();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(image, "png", byteArrayOutputStream);
                byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
                outputStream2.write(size);
                outputStream2.write(byteArrayOutputStream.toByteArray());
                outputStream2.flush();
                outputStream2.close();
            }


            socket2.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
