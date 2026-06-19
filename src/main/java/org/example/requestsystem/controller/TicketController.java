package org.example.requestsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.requestsystem.dto.TicketResponse;
import org.example.requestsystem.service.StoredFileDownload;
import org.example.requestsystem.service.TicketService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/applications")
@Tag(name = "Заявки")
@SecurityRequirement(name = "bearerAuth")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Создать заявку с медиа-файлами")
    public ResponseEntity<TicketResponse> create(
            @Parameter(description = "Название заявки") @RequestParam String title,
            @Parameter(description = "Описание заявки") @RequestParam String description,
            @Parameter(description = "Один или несколько файлов") @RequestParam(value = "files", required = false) List<MultipartFile> files,
            Authentication authentication) {
        TicketResponse response = ticketService.create(authentication.getName(), title, description, files);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Получить свои заявки")
    public List<TicketResponse> findAll(Authentication authentication) {
        return ticketService.findAllForUser(authentication.getName());
    }

    @GetMapping("/{ticketId}")
    @Operation(summary = "Получить свою заявку по id")
    public TicketResponse findOne(@PathVariable Long ticketId, Authentication authentication) {
        return ticketService.findOneForUser(authentication.getName(), ticketId);
    }

    @GetMapping("/{ticketId}/files/{fileId}")
    @Operation(summary = "Скачать файл заявки")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable Long ticketId,
                                                            @PathVariable Long fileId,
                                                            Authentication authentication) {
        StoredFileDownload file = ticketService.downloadFile(authentication.getName(), ticketId, fileId);
        MediaType mediaType = parseMediaType(file.getContentType());
        ContentDisposition contentDisposition = ContentDisposition.attachment()
                .filename(file.getOriginalFileName(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(mediaType)
                .contentLength(file.getSizeBytes())
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .body(new InputStreamResource(file.getInputStream()));
    }

    private MediaType parseMediaType(String contentType) {
        try {
            return MediaType.parseMediaType(contentType);
        } catch (Exception ex) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}
