package site.abely;

import dorkbox.notify.Notify;

import javax.imageio.ImageIO;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
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

import static dorkbox.notify.Pos.TOP_RIGHT;

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
                Notify.create()
                        .title("消息")
                        .text("收到消息")
                        .position(TOP_RIGHT)
                        .hideAfter(500)
                        .showInformation();
                Runtime runtime = Runtime.getRuntime();
                String[] args = { "osascript", "-e", "display notification \"Lorem ipsum dolor sit amet\" with title \"Title\"\n" };
                Process process = runtime.exec(args);

                if (type == ClipInfo.TEXT || type == ClipInfo.IMAGE) {
                    byte[] contentLengthInfo = new byte[4];
                    in.readFully(contentLengthInfo);
                    int size = ByteBuffer.wrap(contentLengthInfo).asIntBuffer().get();
                    byte[] contentInfo = new byte[size];
                    in.readFully(contentInfo);
                    in.close();
                    Notify.create()
                            .title("消息")
                            .text("文本或图片已复制到剪切板")
                            .position(TOP_RIGHT)
                            .showInformation();

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
                    Files.write(Paths.get("/Users/abley/" + name), contentInfo, StandardOpenOption.CREATE_NEW);
                    Notify.create()
                            .title("消息")
                            .text("文件下载完毕")
                            .position(TOP_RIGHT)
                            .hideAfter(2000)
                            .showInformation();
                }

            } catch (IOException e) {
                e.printStackTrace();
                Notify.create()
                        .title("消息")
                        .text("文件接收异常，请查看日志")
                        .position(TOP_RIGHT)
                        .hideAfter(2000)
                        .showInformation();
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
