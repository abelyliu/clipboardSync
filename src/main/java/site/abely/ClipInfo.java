package site.abely;

public class ClipInfo {
    public static final int TEXT = 0;
    public static final int IMAGE = 1;
    public static final int FILE = 2;


    private int type;
    private String fileName;
    private byte[] content;

    public ClipInfo(int type, String fileName, byte[] content) {
        this.type = type;
        this.fileName = fileName;
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
