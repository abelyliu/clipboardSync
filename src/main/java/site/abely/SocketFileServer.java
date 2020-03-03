package site.abely;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
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
    public static int type = 0;
    public static String filename = "";

    @Override
    public void run() {

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(9996);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("START work");
        while (true) {
            try {
                Socket accept = serverSocket.accept();
                System.out.println("received file");
                BufferedInputStream bufferedInputStream = new BufferedInputStream(accept.getInputStream());
                synchronized (SocketServer.class) {
                if (type == 1) {
                    DataInputStream in = new DataInputStream(bufferedInputStream);
                    byte[] sizeAr = new byte[4];
                    bufferedInputStream.read(sizeAr);
                    int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();
                    System.out.println("size is " + size);
                    byte[] imageAr = new byte[size];
                    in.readFully(imageAr);
                    in.close();

                    Files.write(Paths.get("/Users/abley/" + filename), imageAr, StandardOpenOption.CREATE_NEW);

                } else if (type == 2) {
                    DataInputStream in = new DataInputStream(bufferedInputStream);
                    byte[] sizeAr = new byte[4];
                    bufferedInputStream.read(sizeAr);
                    int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();
                    System.out.println("size is " + size);
                    byte[] imageAr = new byte[size];
                    in.readFully(imageAr);
                    in.close();
                    BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageAr));
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new ImageTransferable(image), null);
                    System.out.println("copy finish");
                }
                System.out.println("end file transfer");
                }
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
