package site.abely;

import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.Provider;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Arrays;

public class Test {
    public static void main(String[] args) {
        final Provider provider = Provider.getCurrentProvider(true);
        provider.register(KeyStroke.getKeyStroke("control alt D"), new HotKeyListener() {
            public void onHotKey(HotKey hotKey) {
                System.out.println(hotKey);
                provider.reset();
                provider.stop();
            }
        });
        HotKeyListener listener = new HotKeyListener() {
            public void onHotKey(HotKey hotKey) {
                System.out.println(Thread.currentThread().getId());
                Object imageFromClipboard = null;
                try {
                    imageFromClipboard = getImageFromClipboard();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (UnsupportedFlavorException e) {
                    e.printStackTrace();
                }
                System.out.println(imageFromClipboard.toString());

                System.out.println(hotKey);
            }
        };
        provider.register(KeyStroke.getKeyStroke("alt C"), listener);

    }

    public static Object getImageFromClipboard() throws IOException, UnsupportedFlavorException {
        Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            return (Image) transferable.getTransferData(DataFlavor.imageFlavor);
        } else if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            return transferable.getTransferData(DataFlavor.stringFlavor);
        } else {
            System.err.println("getImageFromClipboard: That wasn't an image!");
        }
        return null;
    }
}
