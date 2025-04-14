package exception;

public class ExistStorageException extends StorageException {
    public ExistStorageException(String uuid) {
        super("Resume with " + uuid + " already exists",uuid);
    }
}
