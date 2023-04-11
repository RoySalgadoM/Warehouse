package mx.edu.utez.warehouse.entry.service;

import mx.edu.utez.warehouse.entry.model.EntryModel;
import mx.edu.utez.warehouse.message.model.MessageModel;
import org.springframework.data.domain.Pageable;

public interface EntryService {
    MessageModel findAllEntries(Pageable page, String username, String uuid);
    MessageModel findById(long id, String username, String uuid);
    MessageModel registerEntry(EntryModel entryModel, String username, String uuid);
    MessageModel cancelEntry(long id, String username, String uuid);
    MessageModel deliveredEntry(long id, String username, String uuid);
}
