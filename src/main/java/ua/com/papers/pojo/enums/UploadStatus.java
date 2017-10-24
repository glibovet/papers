package ua.com.papers.pojo.enums;

/**
 * Created by Максим on 10/23/2017.
 */
public enum UploadStatus {
    UPLOADED(1), FAILED(-1), PENDING(0);

    private final int id;

    UploadStatus(int id) {
        this.id = id;
    }

    public static UploadStatus forId(int id) {
        for (final UploadStatus status : UploadStatus.values()) {
            if (status.id == id) return status;
        }
        return null;
    }
}
