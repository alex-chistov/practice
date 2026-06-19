package org.example.requestsystem.service;

import java.io.InputStream;

public class StoredFileDownload {

    private InputStream inputStream;
    private String originalFileName;
    private String contentType;
    private long sizeBytes;

    public StoredFileDownload(InputStream inputStream, String originalFileName, String contentType, long sizeBytes) {
        this.inputStream = inputStream;
        this.originalFileName = originalFileName;
        this.contentType = contentType;
        this.sizeBytes = sizeBytes;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }
}
