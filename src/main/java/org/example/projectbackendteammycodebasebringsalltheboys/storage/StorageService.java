package org.example.projectbackendteammycodebasebringsalltheboys.storage;

import java.io.InputStream;

public interface StorageService {
    String uploadFile(String fileName, InputStream inputStream, long size, String contentType);

    InputStream downloadFile(String s3Key);

    void deleteFile(String s3Key);
}
