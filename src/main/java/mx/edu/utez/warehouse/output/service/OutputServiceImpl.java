package mx.edu.utez.warehouse.output.service;

import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import mx.edu.utez.warehouse.entry.model.EntryModel;
import mx.edu.utez.warehouse.message.model.MessageModel;
import mx.edu.utez.warehouse.order_status.model.OrderStatusModel;
import mx.edu.utez.warehouse.order_status.service.OrderStatusRepository;
import mx.edu.utez.warehouse.output.model.OutputModel;
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
public class OutputServiceImpl implements OutputService{
    private static final Logger logger = LogManager.getLogger(OutputServiceImpl.class);

    private static final String OUTPUT_NOT_FOUND = "The output could not be found";
    @Autowired
    OutputRepository repository;

    @Autowired
    OrderStatusRepository orderStatusRepository;

    @Autowired
    RequisitionRepository requisitionRepository;
    @Override
    public MessageModel findAllOutputs(Pageable page, String username, String uuid) {
        try {
            Page<OutputModel> outputs = repository.findAll(page);

            if (outputs.getNumberOfElements() == 0) {
                return new MessageModel(MessageCatalog.NO_RECORDS_FOUND, null, false);
            }
            return new MessageModel(MessageCatalog.RECORDS_FOUND, outputs, false);

        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> OUTPUT MODULE ---> findAllOutputs() ERROR: {}", username, uuid, exception.getMessage());
            return new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
    }

    @Override
    public MessageModel findById(long id, String username, String uuid) {
        try {
            var findOutput = repository.findById(id);
            if (findOutput.isEmpty()) {
                throw new NoResultException(OUTPUT_NOT_FOUND);
            }
            return new MessageModel(MessageCatalog.RECORDS_FOUND, findOutput.get(), false);

        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> OUTPUT MODULE ---> findById() ERROR: {}", username, uuid, exception.getMessage());
            return new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
    }

    @Override
    public MessageModel registerOutput(OutputModel outputModel, String username, String uuid) {
        MessageModel messageModel;
        try {
            RequisitionModel requisition = outputModel.getRequisition();
            requisition.setStatus(orderStatusRepository.findById(1L));
            requisition.setTotalAmount(requisition.getTotalAmount());
            requisition = requisitionRepository.saveAndFlush(requisition);

            outputModel.setRequisition(requisition);
            OutputModel outputSave = repository.saveAndFlush(outputModel);
            messageModel = new MessageModel(MessageCatalog.SUCCESS_REGISTER, outputSave, false);
        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> OUTPUT MODULE ---> saveOutput() ERROR: {}", username, uuid, exception.getMessage());
            messageModel = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
        return messageModel;
    }

    @Override
    public MessageModel cancelOutput(long id, String username, String uuid) {
        try {
            var output = repository.findById(id);
            var requisition = requisitionRepository.findById(output.get().getRequisition().getId());
            if (output.isEmpty()) {
                throw new NoResultException(OUTPUT_NOT_FOUND);
            }
            if (requisition.isEmpty()) {
                throw new NoResultException(OUTPUT_NOT_FOUND);
            }
            OrderStatusModel status = orderStatusRepository.findById(4L);
            requisition.get().setStatus(requisition.get().getStatus().getId() == 1L ? status : requisition.get().getStatus());
            requisitionRepository.saveAndFlush(requisition.get());
            return new MessageModel(requisition.get().getStatus().getId() == 4L ? MessageCatalog.SUCCESS_CANCEL : MessageCatalog.ERROR_CANCEL, null, false);
        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> OUTPUT MODULE ---> cancelOutput() ERROR: {}", username, uuid, exception.getMessage());
            return new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
    }

    @Override
    @Transactional
    public MessageModel sentOutput(long id, String username, String uuid) {
        try {
            var output = repository.findById(id);
            var requisition = requisitionRepository.findById(output.get().getRequisition().getId());
            if (output.isEmpty()) {
                throw new NoResultException(OUTPUT_NOT_FOUND);
            }
            if (requisition.isEmpty()) {
                throw new NoResultException(OUTPUT_NOT_FOUND);
            }
            OrderStatusModel status = orderStatusRepository.findById(2L);
            requisition.get().setStatus(requisition.get().getStatus().getId() == 1L ? status : requisition.get().getStatus());
            requisitionRepository.saveAndFlush(requisition.get());
            return new MessageModel(requisition.get().getStatus().getId() == 2L ? MessageCatalog.SUCCESS_SENT : MessageCatalog.ERROR_SENT, null, false);
        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> OUTPUT MODULE ---> sentOutput() ERROR: {}", username, uuid, exception.getMessage());
            return new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
    }

    public boolean isExistOutput(String code) {
        return requisitionRepository.existsByCode(code);
    }
}
