package site.abely;

import dorkbox.notify.Notify;

import java.io.IOException;

import static dorkbox.notify.Pos.TOP_RIGHT;

public class NotificationUtil {

    public static void notification(String title, String content) {
        if (EnvironmentUtil.isMac()) {
            sendMac(title, content);
        } else {
            sendOther(title, content);
        }
    }

    private static void sendMac(String title, String content) {
        Runtime runtime = Runtime.getRuntime();
        String[] args = {"osascript", "-e", "display notification \"" + content + "\" with title \"" + title + "\"\n"};
        try {
            Process process = runtime.exec(args);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendOther(String title, String content) {
        Notify.create()
                .title("title")
                .text("content")
                .position(TOP_RIGHT)
                .hideAfter(2000)
                .showInformation();
    }
}


