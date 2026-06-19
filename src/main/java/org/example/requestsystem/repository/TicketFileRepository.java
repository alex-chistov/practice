package org.example.requestsystem.repository;

import org.example.requestsystem.model.TicketFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TicketFileRepository extends JpaRepository<TicketFile, Long> {

    Optional<TicketFile> findByIdAndTicketIdAndTicketOwnerUsername(Long id, Long ticketId, String username);
}
