package org.example.requestsystem.service;

import org.example.requestsystem.dto.TicketFileResponse;
import org.example.requestsystem.dto.TicketResponse;
import org.example.requestsystem.model.AppUser;
import org.example.requestsystem.model.Ticket;
import org.example.requestsystem.model.TicketCategory;
import org.example.requestsystem.model.TicketFile;
import org.example.requestsystem.repository.TicketFileRepository;
import org.example.requestsystem.repository.TicketRepository;
import org.example.requestsystem.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketFileRepository ticketFileRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public TicketService(TicketRepository ticketRepository,
                         TicketFileRepository ticketFileRepository,
                         UserRepository userRepository,
                         FileStorageService fileStorageService) {
        this.ticketRepository = ticketRepository;
        this.ticketFileRepository = ticketFileRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public TicketResponse create(String username, String title, String description, List<MultipartFile> files) {
        if (title == null || title.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Название заявки обязательно");
        }
        if (description == null || description.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Описание заявки обязательно");
        }

        AppUser owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Пользователь не найден"));

        Ticket ticket = new Ticket();
        ticket.setOwner(owner);
        ticket.setTitle(title.trim());
        ticket.setDescription(description.trim());
        ticket = ticketRepository.save(ticket);

        for (MultipartFile file : safeFiles(files)) {
            if (file.isEmpty()) {
                continue;
            }
            StoredFile storedFile = fileStorageService.upload(ticket.getId(), file);

            TicketFile ticketFile = new TicketFile();
            ticketFile.setOriginalFileName(storedFile.getOriginalFileName());
            ticketFile.setContentType(storedFile.getContentType());
            ticketFile.setSizeBytes(storedFile.getSizeBytes());
            ticketFile.setObjectKey(storedFile.getObjectKey());
            ticket.addFile(ticketFile);
        }

        Ticket savedTicket = ticketRepository.save(ticket);
        return toResponse(savedTicket);
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> findAllForUser(String username) {
        return ticketRepository.findByOwnerUsernameOrderByCreatedAtDesc(username)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TicketResponse findOneForUser(String username, Long ticketId) {
        Ticket ticket = ticketRepository.findByIdAndOwnerUsername(ticketId, username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Заявка не найдена"));
        return toResponse(ticket);
    }

    @Transactional(readOnly = true)
    public StoredFileDownload downloadFile(String username, Long ticketId, Long fileId) {
        TicketFile file = ticketFileRepository.findByIdAndTicketIdAndTicketOwnerUsername(fileId, ticketId, username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Файл не найден"));
        return fileStorageService.download(file);
    }

    private List<MultipartFile> safeFiles(List<MultipartFile> files) {
        return files == null ? Collections.emptyList() : files;
    }

    private TicketResponse toResponse(Ticket ticket) {
        List<TicketFileResponse> files = ticket.getFiles()
                .stream()
                .map(file -> new TicketFileResponse(
                        file.getId(),
                        file.getOriginalFileName(),
                        file.getContentType(),
                        file.getSizeBytes(),
                        "/api/applications/" + ticket.getId() + "/files/" + file.getId()
                ))
                .toList();

        return new TicketResponse(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getCategory() == null ? TicketCategory.OTHER : ticket.getCategory(),
                ticket.getStatus(),
                ticket.getCreatedAt(),
                files
        );
    }
}
