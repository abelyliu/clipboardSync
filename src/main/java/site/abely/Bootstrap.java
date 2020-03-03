package site.abely;

import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.Provider;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Bootstrap {
    public static void main(String[] args) {
        new Thread(new SocketServer()).start();
        new Thread(new SocketFileServer()).start();
        final Provider provider = Provider.getCurrentProvider(true);
        HotKeyListener listener = hotKey -> {
            System.out.println(Thread.currentThread().getId());
            Object imageFromClipboard = null;
            try {
                imageFromClipboard = getImageFromClipboard();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (UnsupportedFlavorException e) {
                e.printStackTrace();
            }
            SendService sendService = new SendService();
            if (imageFromClipboard instanceof String) {
                sendService.send(0, (String) imageFromClipboard, null, null);
            } else if (imageFromClipboard instanceof Tuple) {
                sendService.send(1, (String) ((Tuple) imageFromClipboard).x, (byte[]) (((Tuple) imageFromClipboard).y), null);
            } else if (imageFromClipboard instanceof BufferedImage) {
                sendService.send(2, null, null, (BufferedImage) imageFromClipboard);
            }
        };
        provider.register(KeyStroke.getKeyStroke("alt C"), listener);
    }

    public static Object getImageFromClipboard() throws IOException, UnsupportedFlavorException {
        Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            return (BufferedImage) transferable.getTransferData(DataFlavor.imageFlavor);
        } else if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            String filePath = (String) transferable.getTransferData(DataFlavor.stringFlavor);
            byte[] bytes = Files.readAllBytes(Paths.get(filePath));
            return new Tuple<>(filePath, bytes);
        } else if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            return transferable.getTransferData(DataFlavor.stringFlavor);
        } else {
            System.err.println("getImageFromClipboard: That wasn't an image!");
        }
        return null;
    }
}
