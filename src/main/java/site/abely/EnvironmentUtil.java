package site.abely;

public class EnvironmentUtil {

    public static boolean isMac() {
        return System.getProperty("os.name").toLowerCase().contains("mac");
    }

    public static boolean isWindows() {
        return false;
    }

    public static boolean isLinux() {
        return false;
    }

}
