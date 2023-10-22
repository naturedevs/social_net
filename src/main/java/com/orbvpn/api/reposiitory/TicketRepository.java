package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.entity.Ticket;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.domain.enums.TicketCategory;
import com.orbvpn.api.domain.enums.TicketStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TicketRepository extends JpaRepository<Ticket, Integer> {
  Page<Ticket> findAllByCategory(TicketCategory category, Pageable pageable);

  Page<Ticket> findAllByStatus(TicketStatus status, Pageable pageable);

  Page<Ticket> findAllByStatusAndCategory(TicketStatus status, TicketCategory category,  Pageable pageable);

  List<Ticket> findByCreator(User creator);

  void deleteByCreatedAtBefore(LocalDateTime dateTime);
}
