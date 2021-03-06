package site.abely;

import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.Provider;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Bootstrap {
    public static boolean listener = true;

    public static void main(String[] args) {
        //如果开启了代理，会导致发送udp广播失败，因为场景是局域网内传输，所以这里强制不使用代理
        System.setProperty("http.proxyHost", "");
        System.setProperty("http.proxyPort", "");
        UDPClient.run(UDPClient.message1);
        UDPServer.run();
        new Thread(new SocketFileServer()).start();
        //这里
        final Provider provider = Provider.getCurrentProvider(true);
        HotKeyListener listener = hotKey -> {
            send();
        };
        //加下面这一行是mac os的问题，加上之后会在dock里面出现个java进程，挺尬的
        Toolkit.getDefaultToolkit();
        provider.register(KeyStroke.getKeyStroke("alt C"), listener);
        Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        systemClipboard.addFlavorListener(e -> send());
    }

    private static void send() {
        if (!listener) return;
        System.out.println(LocalTime.now() + "");
        ClipInfo imageFromClipboard = null;
        try {
            imageFromClipboard = getImageFromClipboard();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedFlavorException e) {
            e.printStackTrace();
        }
        if (imageFromClipboard == null) {
            System.err.println("not support type");
        }
        SendService sendService = new SendService();
        sendService.send(imageFromClipboard);
    }

    public static ClipInfo getImageFromClipboard() throws IOException, UnsupportedFlavorException {
        Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            java.util.List<File> filePath = (java.util.List<File>) transferable.getTransferData((DataFlavor.javaFileListFlavor));
            System.out.println("file path is " + filePath);
//            File file = filePath.get(0);
//            long length = file.length();
//            byte[] bytes = Files.readAllBytes(filePath.get(0).toPath());
//            System.out.println(bytes.length== ((int) (length)));
            return new ClipInfo(ClipInfo.FILE, filePath.get(0), null);
        } else if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            BufferedImage image = getImage((Image) transferable.getTransferData(DataFlavor.imageFlavor));
            System.out.println(LocalTime.now());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", byteArrayOutputStream);
            return new ClipInfo(ClipInfo.IMAGE, null, byteArrayOutputStream.toByteArray());
        } else if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            String transferData = (String) transferable.getTransferData(DataFlavor.stringFlavor);
            return new ClipInfo(ClipInfo.TEXT, null, transferData.getBytes(StandardCharsets.UTF_8));
        } else {
            System.err.println("not support type");
        }
        return null;
    }

    //mac os 会返回MultiResolutionCachedImage，这里先兼容下
    public static BufferedImage getImage(Image image) {
        if (image instanceof BufferedImage) return (BufferedImage) image;
        Lock lock = new ReentrantLock();
        Condition size = lock.newCondition(), data = lock.newCondition();
        ImageObserver o = (img, infoflags, x, y, width, height) -> {
            lock.lock();
            try {
                if ((infoflags & ImageObserver.ALLBITS) != 0) {
                    size.signal();
                    data.signal();
                    return false;
                }
                if ((infoflags & (ImageObserver.WIDTH | ImageObserver.HEIGHT)) != 0)
                    size.signal();
                return true;
            } finally {
                lock.unlock();
            }
        };
        BufferedImage bi;
        lock.lock();
        try {
            int width, height = 0;
            while ((width = image.getWidth(o)) < 0 || (height = image.getHeight(o)) < 0)
                size.awaitUninterruptibly();
            bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = bi.createGraphics();
            try {
                g.setBackground(new Color(0, true));
                g.clearRect(0, 0, width, height);
                while (!g.drawImage(image, 0, 0, o)) data.awaitUninterruptibly();
            } finally {
                g.dispose();
            }
        } finally {
            lock.unlock();
        }
        return bi;
    }
}
