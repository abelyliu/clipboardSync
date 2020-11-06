package site.abely;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClipInfo clipInfo = (ClipInfo) o;
        return type == clipInfo.type &&
                Objects.equals(file, clipInfo.file) &&
                Arrays.equals(content, clipInfo.content);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(type, file);
        result = 31 * result + Arrays.hashCode(content);
        return result;
    }
}
