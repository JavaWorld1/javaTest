package com.webapp.storage;

import com.webapp.exception.NotExistStorageException;
import com.webapp.exception.StorageException;
import com.webapp.model.Resume;

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