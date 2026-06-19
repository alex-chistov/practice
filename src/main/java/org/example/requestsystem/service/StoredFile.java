package org.example.requestsystem.service;

public class StoredFile {

    private String objectKey;
    private String originalFileName;
    private String contentType;
    private long sizeBytes;

    public StoredFile(String objectKey, String originalFileName, String contentType, long sizeBytes) {
        this.objectKey = objectKey;
        this.originalFileName = originalFileName;
        this.contentType = contentType;
        this.sizeBytes = sizeBytes;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
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
