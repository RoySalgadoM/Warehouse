package mx.edu.utez.warehouse.requisition.service;

import jakarta.persistence.NoResultException;
import mx.edu.utez.warehouse.message.model.MessageModel;
import mx.edu.utez.warehouse.requisition.model.RequisitionModel;
import mx.edu.utez.warehouse.utils.MessageCatalog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class RequisitionServiceImpl implements RequisitionService{
    private static final Logger logger = LogManager.getLogger(RequisitionServiceImpl.class);

    @Autowired
    RequisitionRepository repository;
    @Override
    public RequisitionModel registerRequisition(RequisitionModel requisitionModel) {
        return null;
    }

    @Override
    public MessageModel findAllRequisitions(Pageable page, String username, String uuid) {
        try {
            Page<RequisitionModel> requisitions = repository.findAll(page);

            if (requisitions.getNumberOfElements() == 0) {
                return new MessageModel(MessageCatalog.NO_RECORDS_FOUND, null, false);
            }
            return new MessageModel(MessageCatalog.RECORDS_FOUND, requisitions, false);

        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> REQUISITION MODULE ---> findAllRequisitions() ERROR: {}", username, uuid, exception.getMessage());
            return new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
    }

    @Override
    public MessageModel findById(long id, String username, String uuid) {
        try {
            var findEntry = repository.findById(id);
            if (findEntry.isEmpty()) {
                throw new NoResultException("The requisition could not be found");
            }
            return new MessageModel(MessageCatalog.RECORDS_FOUND, findEntry.get(), false);

        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> REQUISITION MODULE ---> findById() ERROR: {}", username, uuid, exception.getMessage());
            return new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
    }
}
