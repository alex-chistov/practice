package org.example.requestsystem.repository;

import org.example.requestsystem.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByOwnerUsernameOrderByCreatedAtDesc(String username);

    Optional<Ticket> findByIdAndOwnerUsername(Long id, String username);
}
