package site.abely;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class SocketFileServer implements Runnable {


    @Override
    public void run() {

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(9997);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("START work");
        while (true) {
            try {
                Socket accept = serverSocket.accept();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(accept.getInputStream());
                DataInputStream in = new DataInputStream(bufferedInputStream);
                byte[] typeInfo = new byte[1];
                in.readFully(typeInfo);
                int type = ByteBuffer.wrap(typeInfo).get();
//                NotificationUtil.notification("消息", "开始接收消息");
                if (type == ClipInfo.TEXT || type == ClipInfo.IMAGE) {
                    byte[] contentLengthInfo = new byte[4];
                    in.readFully(contentLengthInfo);
                    int size = ByteBuffer.wrap(contentLengthInfo).asIntBuffer().get();
                    byte[] contentInfo = new byte[size];
                    in.readFully(contentInfo);
                    in.close();
                    NotificationUtil.notification("消息", "接收到文本图片消息完成");

                    if (type == ClipInfo.TEXT) {
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(new String(contentInfo, "utf-8")), null);
                    } else if (type == ClipInfo.IMAGE) {
                        BufferedImage image = ImageIO.read(new ByteArrayInputStream(contentInfo));
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new ImageTransferable(image), null);
                    }

                } else if (type == ClipInfo.FILE) {
                    byte[] nameLengthInfo = new byte[4];
                    in.readFully(nameLengthInfo);
                    int size = ByteBuffer.wrap(nameLengthInfo).asIntBuffer().get();
                    byte[] nameInfo = new byte[size];
                    in.readFully(nameInfo);
                    String name = new String(nameInfo, "utf-8");
                    System.out.println("file name is " + name);
                    byte[] contentLengthInfo = new byte[4];
                    in.readFully(contentLengthInfo);
                    int contentSize = ByteBuffer.wrap(contentLengthInfo).asIntBuffer().get();
                    byte[] contentInfo = new byte[contentSize];
                    in.readFully(contentInfo);
                    Files.write(Paths.get(System.getProperty("user.home")+"/" + name), contentInfo, StandardOpenOption.CREATE_NEW);
                    NotificationUtil.notification("消息", "接收到文件消息");
                }
                System.out.println("接收消息完成");

            } catch (IOException e) {
                e.printStackTrace();
                NotificationUtil.notification("消息", "接收消息异常");
            }
        }

    }


    static class ImageTransferable implements Transferable {
        private Image image;

        public ImageTransferable(Image image) {
            this.image = image;
        }

        @Override
        public Object getTransferData(DataFlavor flavor)
                throws UnsupportedFlavorException {
            if (isDataFlavorSupported(flavor)) {
                return image;
            } else {
                throw new UnsupportedFlavorException(flavor);
            }
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor == DataFlavor.imageFlavor;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DataFlavor.imageFlavor};
        }
    }
}
