package site.abely;

import java.io.File;

public class ClipInfo {
    public static final int TEXT = 0;
    public static final int IMAGE = 1;
    public static final int FILE = 2;


    private int type;
    private File file;
    private byte[] content;

    public ClipInfo(int type, File file, byte[] content) {
        this.type = type;
        this.file = file;
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
