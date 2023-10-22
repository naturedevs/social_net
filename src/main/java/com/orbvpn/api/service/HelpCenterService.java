package com.orbvpn.api.service;

import static com.orbvpn.api.config.AppConstants.DEFAULT_SORT;

import com.orbvpn.api.domain.dto.TicketCreate;
import com.orbvpn.api.domain.dto.TicketReplyCreate;
import com.orbvpn.api.domain.dto.TicketReplyView;
import com.orbvpn.api.domain.dto.TicketView;
import com.orbvpn.api.domain.entity.Role;
import com.orbvpn.api.domain.entity.Ticket;
import com.orbvpn.api.domain.entity.TicketReply;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.domain.enums.RoleName;
import com.orbvpn.api.domain.enums.TicketCategory;
import com.orbvpn.api.domain.enums.TicketStatus;
import com.orbvpn.api.exception.NotFoundException;
import com.orbvpn.api.mapper.TicketEditMapper;
import com.orbvpn.api.mapper.TicketReplyEditMapper;
import com.orbvpn.api.mapper.TicketReplyViewMapper;
import com.orbvpn.api.mapper.TicketViewMapper;
import com.orbvpn.api.reposiitory.TicketReplyRepository;
import com.orbvpn.api.reposiitory.TicketRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class HelpCenterService {

  private final UserService userService;
  private final TicketRepository ticketRepository;
  private final TicketReplyRepository ticketReplyRepository;
  private final TicketEditMapper ticketEditMapper;
  private final TicketViewMapper ticketViewMapper;
  private final TicketReplyEditMapper ticketReplyEditMapper;
  private final TicketReplyViewMapper ticketReplyViewMapper;

  public TicketView createTicket(TicketCreate ticketCreate) {
    log.info("Creating ticket {}", ticketCreate);

    Ticket ticket = ticketEditMapper.create(ticketCreate);
    User creator = userService.getUser();
    ticket.setCreator(creator);
    ticket.setStatus(TicketStatus.OPEN);
    ticketRepository.save(ticket);
    TicketView ticketView = ticketViewMapper.toView(ticket);

    log.info("Created ticket {}", ticketView);

    return ticketView;
  }

  public TicketView getTicketView(int id) {
    Ticket ticket = getTicket(id);
    return ticketViewMapper.toView(ticket);
  }

  public Page<TicketView> getTickets(int page, int size, TicketCategory category,
    TicketStatus status) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(DEFAULT_SORT));
    Page<Ticket> queryResult;

    if (category == null && status == null) {
      queryResult = ticketRepository.findAll(pageable);
    } else if (category != null && status == null) {
      queryResult = ticketRepository.findAllByCategory(category, pageable);
    } else if (category == null && status != null) {
      queryResult = ticketRepository.findAllByStatus(status, pageable);
    } else {
      queryResult = ticketRepository.findAllByStatusAndCategory(status, category, pageable);
    }

    return queryResult.map(ticketViewMapper::toView);
  }

  public TicketView closeTicket(int id) {
    log.info("Editing ticket with id {} with data {}", id);

    Ticket ticket = getTicket(id);

    User user = userService.getUser();
    if (user.getRole().getName() == RoleName.USER && ticket.getCreator().getId() != user.getId()) {
      throw new AccessDeniedException("Can't close ticket");
    }

    ticket.setStatus(TicketStatus.CLOSED);
    ticketRepository.save(ticket);

    TicketView ticketView = ticketViewMapper.toView(ticket);

    log.info("Deleted ticket {}", ticketView);

    return ticketView;
  }

  public List<TicketView> closeTickets(List<Integer> ids) {
    log.info("Closing tickets");

    List<Ticket> tickets = ticketRepository.findAllById(ids);
    tickets.forEach(t -> {
      t.setStatus(TicketStatus.CLOSED);
    });
    ticketRepository.saveAll(tickets);

    return tickets.stream()
      .map(ticketViewMapper::toView)
      .collect(Collectors.toList());
  }

  public TicketView deleteTicket(int id) {
    log.info("Deleting ticket with id {}", id);

    Ticket ticket = getTicket(id);
    ticketRepository.delete(ticket);

    TicketView ticketView = ticketViewMapper.toView(ticket);

    log.info("Returning ticket {}", ticketView);

    return ticketView;
  }

  public TicketReplyView replyToTicket(int ticketId, TicketReplyCreate ticketReplyCreate) {
    log.info("Replying to ticket with id {} with data {}", ticketReplyCreate);

    Ticket ticket = getTicket(ticketId);
    User creator = userService.getUser();
    Role role = creator.getRole();

    if (role.getName() == RoleName.USER && creator.getId() != ticket.getCreator().getId()) {
      throw new AccessDeniedException("Can't reply to another customers message");
    }

    TicketReply ticketReply = ticketReplyEditMapper.create(ticketReplyCreate);
    ticketReply.setTicket(ticket);
    ticketReply.setCreator(creator);

    TicketStatus status = TicketStatus.ANSWERED;
    if (role.getName() == RoleName.USER) {
      status = TicketStatus.CUSTOMER_REPLY;
    }
    ticket.setStatus(status);

    ticketReplyRepository.save(ticketReply);
    TicketReplyView ticketReplyView = ticketReplyViewMapper.toView(ticketReply);

    log.info("Created ticket reply {}", ticketReplyView);

    return ticketReplyView;
  }

  public void removeOldTickets() {
    LocalDateTime monthBefore = LocalDateTime.now().minusMonths(1L);
    ticketRepository.deleteByCreatedAtBefore(monthBefore);
  }

  public List<TicketView> getUserTickets() {
    User user = userService.getUser();
    return ticketRepository.findByCreator(user)
      .stream()
      .map(ticketViewMapper::toView)
      .collect(Collectors.toList());
  }

  public Ticket getTicket(int id) {
    return ticketRepository.findById(id)
      .orElseThrow(() -> new NotFoundException(Ticket.class, id));
  }
}
