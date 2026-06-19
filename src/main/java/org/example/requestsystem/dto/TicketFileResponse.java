package org.example.requestsystem.dto;

public class TicketFileResponse {

    private Long id;
    private String originalFileName;
    private String contentType;
    private Long sizeBytes;
    private String downloadUrl;

    public TicketFileResponse(Long id, String originalFileName, String contentType, Long sizeBytes, String downloadUrl) {
        this.id = id;
        this.originalFileName = originalFileName;
        this.contentType = contentType;
        this.sizeBytes = sizeBytes;
        this.downloadUrl = downloadUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(Long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
