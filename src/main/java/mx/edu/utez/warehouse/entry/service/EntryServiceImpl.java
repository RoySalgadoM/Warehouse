package mx.edu.utez.warehouse.entry.service;

import jakarta.persistence.NoResultException;
import mx.edu.utez.warehouse.area.service.AreaServiceImpl;
import mx.edu.utez.warehouse.entry.model.EntryModel;
import mx.edu.utez.warehouse.message.model.MessageModel;
import mx.edu.utez.warehouse.utils.MessageCatalog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class EntryServiceImpl implements EntryService{
    private static final Logger logger = LogManager.getLogger(AreaServiceImpl.class);

    @Autowired
    EntryRepository repository;

    @Override
    public MessageModel findAllEntries(Pageable page, String username, String uuid) {
        try {
            Page<EntryModel> entries = repository.findAll(page);

            if (entries.getNumberOfElements() == 0) {
                return new MessageModel(MessageCatalog.NO_RECORDS_FOUND, null, false);
            }
            return new MessageModel(MessageCatalog.RECORDS_FOUND, entries, false);

        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> ENTRY MODULE ---> findAllEntries() ERROR: {}", username, uuid, exception.getMessage());
            return new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
    }

    @Override
    public MessageModel findById(long id, String username, String uuid) {
        try {
            var findEntry = repository.findById(id);
            if (findEntry.isEmpty()) {
                throw new NoResultException("The entry could not be found");
            }
            return new MessageModel(MessageCatalog.RECORDS_FOUND, findEntry.get(), false);

        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> ENTRY MODULE ---> findById() ERROR: {}", username, uuid, exception.getMessage());
            return new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
    }

    @Override
    public MessageModel registerEntry(EntryModel entryModel, String username, String uuid) {
        return null;
    }

    @Override
    public MessageModel updateEntry(EntryModel entryModel, String username, String uuid) {
        return null;
    }
}
