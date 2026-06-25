package org.example.requestsystem.dto;

import org.example.requestsystem.model.TicketCategory;
import org.example.requestsystem.model.TicketStatus;

import java.time.Instant;
import java.util.List;

public class TicketResponse {

    private Long id;
    private String title;
    private String description;
    private TicketCategory category;
    private TicketStatus status;
    private Instant createdAt;
    private List<TicketFileResponse> files;

    public TicketResponse(Long id, String title, String description, TicketCategory category, TicketStatus status,
                          Instant createdAt, List<TicketFileResponse> files) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.status = status;
        this.createdAt = createdAt;
        this.files = files;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TicketCategory getCategory() {
        return category;
    }

    public void setCategory(TicketCategory category) {
        this.category = category;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public List<TicketFileResponse> getFiles() {
        return files;
    }

    public void setFiles(List<TicketFileResponse> files) {
        this.files = files;
    }
}
