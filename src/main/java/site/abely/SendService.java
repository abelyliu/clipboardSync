package site.abely;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

public class SendService {


    public void send(ClipInfo info) {

        SocketAddress socketAddress = new InetSocketAddress("127.0.0.1", 9997);
        try {
            Socket socket = new Socket();
            socket.connect(socketAddress);
            OutputStream outputStream = socket.getOutputStream();
            int type = info.getType();
            byte[] typeInfo = ByteBuffer.allocate(1).put((byte) type).array();
            byte[] contentLength = ByteBuffer.allocate(4).putInt(info.getContent().length).array();
            if (type == ClipInfo.TEXT || type == ClipInfo.IMAGE) {
                outputStream.write(typeInfo);
                outputStream.write(contentLength);
                outputStream.write(info.getContent());
            } else if (type == ClipInfo.FILE) {
                byte[] fileNameInfo = info.getFileName().getBytes("utf-8");
                byte[] fileNameLength = ByteBuffer.allocate(4).putInt(fileNameInfo.length).array();
                outputStream.write(typeInfo);
                outputStream.write(fileNameLength);
                outputStream.write(fileNameInfo);
                outputStream.write(contentLength);
                outputStream.write(info.getContent());
            }
            outputStream.flush();
            outputStream.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
