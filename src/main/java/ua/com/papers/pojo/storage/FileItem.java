package ua.com.papers.pojo.storage;

/**
 * Created by oleh_kurpiak on 01.10.2016.
 */
public class FileItem {

    public String name;

    public ItemType type;

    public String path;

    public FileItem(String name, ItemType type, String path) {
        this.name = name;
        this.type = type;
        this.path = path;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("FileItem{");
        sb.append("name='").append(name).append('\'');
        sb.append(", type=").append(type);
        sb.append(", path='").append(path).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
