package mx.edu.utez.warehouse.requisition.service;

import mx.edu.utez.warehouse.requisition.model.RequisitionModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
}
