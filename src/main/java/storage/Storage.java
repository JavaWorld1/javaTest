package storage;

import exception.NotExistStorageException;
import exception.StorageException;
import model.Resume;

import java.util.List;

public interface Storage {
    void update(Resume resume) throws NotExistStorageException;

    void clear();

    void save(Resume resume) throws StorageException;

    Resume get(String uuid) throws NotExistStorageException;

    void delete(String uuid) throws NotExistStorageException;

    List<Resume> getAllSorted();

    int size();
}