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
                System.out.println("received file");
                BufferedInputStream bufferedInputStream = new BufferedInputStream(accept.getInputStream());
                DataInputStream in = new DataInputStream(bufferedInputStream);
                byte[] typeInfo = new byte[1];
                in.readFully(typeInfo);
//                bufferedInputStream.read(typeInfo);
                int type = ByteBuffer.wrap(typeInfo).get();
                System.out.println("=====");
                System.out.println(type);

                if (type == ClipInfo.TEXT || type == ClipInfo.IMAGE) {
                    byte[] contentLengthInfo = new byte[4];
                    in.readFully(contentLengthInfo);
//                    buffer
//                    edInputStream.read(contentLengthInfo);
                    int size = ByteBuffer.wrap(contentLengthInfo).asIntBuffer().get();
                    byte[] contentInfo = new byte[size];
                    in.readFully(contentInfo);
                    in.close();

                    if (type == ClipInfo.TEXT) {
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(new String(contentInfo, "utf-8")), null);
                    } else if (type == ClipInfo.IMAGE) {
                        BufferedImage image = ImageIO.read(new ByteArrayInputStream(contentInfo));
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new ImageTransferable(image), null);
                    }

                } else if (type == ClipInfo.FILE) {
                    byte[] nameLengthInfo = new byte[4];
                    in.readFully(nameLengthInfo);
//                    bufferedInputStream.read(contentLengthInfo);
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
                }

//                synchronized (SocketFileServer.class) {
//                if (type == 1) {
//                    DataInputStream in = new DataInputStream(bufferedInputStream);
//                    byte[] sizeAr = new byte[4];
//                    bufferedInputStream.read(sizeAr);
//                    int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();
//                    System.out.println("size is " + size);
//                    byte[] imageAr = new byte[size];
//                    in.readFully(imageAr);
//                    in.close();
//
//                    Files.write(Paths.get("/Users/abley/" + filename), imageAr, StandardOpenOption.CREATE_NEW);
//
//                } else if (type == 2) {
//                    DataInputStream in = new DataInputStream(bufferedInputStream);
//                    byte[] sizeAr = new byte[4];
//                    bufferedInputStream.read(sizeAr);
//                    int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();
//                    System.out.println("size is " + size);
//                    byte[] imageAr = new byte[size];
//                    in.readFully(imageAr);
//                    in.close();
//                    BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageAr));
//                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new ImageTransferable(image), null);
//                    System.out.println("copy finish");
//                }
//                System.out.println("end file transfer");
//                }
            } catch (IOException e) {
                e.printStackTrace();
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
