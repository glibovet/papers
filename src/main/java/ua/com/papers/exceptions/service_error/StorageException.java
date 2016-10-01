package ua.com.papers.exceptions.service_error;


/**
 * Created by oleh_kurpiak on 01.10.2016.
 */
public class StorageException extends ServiceErrorException {

    private Exception cause;

    public StorageException(Exception cause){
        this.cause = cause;
    }

    @Override
    public Exception getCause() {
        return cause;
    }
}
