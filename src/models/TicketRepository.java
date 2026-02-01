package models;

import java.util.List;

public interface TicketRepository {
    List<TicketRecord> findAll();
}
