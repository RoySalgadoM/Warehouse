package mx.edu.utez.warehouse.entry.service;

import jakarta.persistence.NoResultException;
import mx.edu.utez.warehouse.entry.model.EntryModel;
import mx.edu.utez.warehouse.message.model.MessageModel;
import mx.edu.utez.warehouse.order_status.model.OrderStatusModel;
import mx.edu.utez.warehouse.order_status.service.OrderStatusRepository;
import mx.edu.utez.warehouse.requisition.model.RequisitionModel;
import mx.edu.utez.warehouse.requisition.service.RequisitionRepository;
import mx.edu.utez.warehouse.utils.MessageCatalog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class EntryServiceImpl implements EntryService{
    private static final Logger logger = LogManager.getLogger(EntryServiceImpl.class);

    private static final String ENTRY_NOT_FOUND = "The entry could not be found";
    @Autowired
    EntryRepository repository;

    @Autowired
    OrderStatusRepository orderStatusRepository;

    @Autowired
    RequisitionRepository requisitionRepository;

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
                throw new NoResultException(ENTRY_NOT_FOUND);
            }
            return new MessageModel(MessageCatalog.RECORDS_FOUND, findEntry.get(), false);

        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> ENTRY MODULE ---> findById() ERROR: {}", username, uuid, exception.getMessage());
            return new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
    }

    @Override
    public MessageModel registerEntry(EntryModel entryModel, String username, String uuid) {
        MessageModel messageModel;
        try {
            RequisitionModel requisition = entryModel.getRequisition();
            requisition.setStatus(orderStatusRepository.findById(1L));
            requisition.setTotalAmount(requisition.getTotalAmount());
            requisition = requisitionRepository.saveAndFlush(requisition);

            entryModel.setRequisition(requisition);
            EntryModel entrySave = repository.saveAndFlush(entryModel);
            messageModel = new MessageModel(MessageCatalog.SUCCESS_REGISTER, entrySave, false);
        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> ENTRY MODULE ---> saveEntry() ERROR: {}", username, uuid, exception.getMessage());
            messageModel = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
        return messageModel;
    }

    @Override
    public MessageModel cancelEntry(long id, String username, String uuid) {
        try {
            var entry = repository.findById(id);
            var requisition = requisitionRepository.findById(entry.get().getRequisition().getId());
            if (entry.isEmpty()) {
                throw new NoResultException(ENTRY_NOT_FOUND);
            }
            if (requisition.isEmpty()) {
                throw new NoResultException(ENTRY_NOT_FOUND);
            }
            OrderStatusModel status = orderStatusRepository.findById(4L);
            requisition.get().setStatus(requisition.get().getStatus().getId() == 1L ? status : requisition.get().getStatus());
            requisitionRepository.saveAndFlush(requisition.get());
            return new MessageModel(requisition.get().getStatus().getId() == 4L ? MessageCatalog.SUCCESS_CANCEL : MessageCatalog.ERROR_CANCEL, null, false);
        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> ENTRY MODULE ---> cancelEntry() ERROR: {}", username, uuid, exception.getMessage());
            return new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
    }

    @Override
    public MessageModel deliveredEntry(long id, String username, String uuid) {
        try {
            var entry = repository.findById(id);
            var requisition = requisitionRepository.findById(entry.get().getRequisition().getId());
            if (entry.isEmpty()) {
                throw new NoResultException(ENTRY_NOT_FOUND);
            }
            if (requisition.isEmpty()) {
                throw new NoResultException(ENTRY_NOT_FOUND);
            }
            OrderStatusModel status = orderStatusRepository.findById(3L);
            requisition.get().setStatus(requisition.get().getStatus().getId() == 1L ? status : requisition.get().getStatus());
            requisitionRepository.saveAndFlush(requisition.get());
            return new MessageModel(requisition.get().getStatus().getId() == 3L ? MessageCatalog.SUCCESS_DELIVERED : MessageCatalog.ERROR_DELIVERED, null, false);
        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> ENTRY MODULE ---> deliveredEntry() ERROR: {}", username, uuid, exception.getMessage());
            return new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
    }

    public boolean isExistEntry(String code) {
        return requisitionRepository.existsByCode(code);
    }
}
