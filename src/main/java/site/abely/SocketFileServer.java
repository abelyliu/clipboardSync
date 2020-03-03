package site.abely;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class SocketFileServer implements Runnable {
    public static int type = 0;

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(9996);
            System.out.println("START work");
            while (true) {
                Socket accept = serverSocket.accept();
                synchronized (SocketServer.class) {
                    System.out.println("received file");
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(accept.getInputStream());
                    if (type == 1) {
                        File targetFile = new File("src/main/resources/targetFile.png");
                        OutputStream outStream = new FileOutputStream(targetFile);
                        byte[] buffer = new byte[8 * 1024];
                        int bytesRead;
                        while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
                            outStream.write(buffer, 0, bytesRead);
                        }
                        outStream.close();
                    } else if (type == 2) {
                        byte[] sizeAr = new byte[4];
                        bufferedInputStream.read(sizeAr);
                        int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();

                        byte[] imageAr = new byte[size];
                        bufferedInputStream.read(imageAr);
                        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageAr));
                        System.out.println("Received " + image.getHeight() + "x" + image.getWidth() + ": " + System.currentTimeMillis());
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new ImageTransferable(image), null);

                    }
                    System.out.println("end file transfer");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    static class ImageTransferable implements Transferable {
        private Image image;

        public ImageTransferable(Image image) {
            this.image = image;
        }

        public Object getTransferData(DataFlavor flavor)
                throws UnsupportedFlavorException {
            if (isDataFlavorSupported(flavor)) {
                return image;
            } else {
                throw new UnsupportedFlavorException(flavor);
            }
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor == DataFlavor.imageFlavor;
        }

        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DataFlavor.imageFlavor};
        }
    }
}
