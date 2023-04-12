package mx.edu.utez.warehouse.requisition.service;

import mx.edu.utez.warehouse.message.model.MessageModel;
import mx.edu.utez.warehouse.requisition.model.RequisitionModel;
import org.springframework.data.domain.Pageable;

public interface RequisitionService {
    RequisitionModel registerRequisition(RequisitionModel requisitionModel);
    MessageModel findAllRequisitions(Pageable page, String username, String uuid);
    MessageModel findById(long id, String username, String uuid);
}
