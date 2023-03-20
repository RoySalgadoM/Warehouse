package mx.edu.utez.warehouse.area.service;

import mx.edu.utez.warehouse.area.model.AreaModel;
import mx.edu.utez.warehouse.message.model.MessageModel;
import mx.edu.utez.warehouse.utils.MessageCatalog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AreaServiceImpl implements AreaService {
    private static final Logger logger = LogManager.getLogger(AreaServiceImpl.class);

    @Autowired
    AreaRepository repository;

    @Override
    public MessageModel findAllAreas(int page, int size) {
        try{
            Pageable pageable = PageRequest.of(page, size);
            Page<AreaModel> areas = repository.findAll(pageable);

            if(areas.getNumberOfElements() == 0){
                return new MessageModel(MessageCatalog.NO_RECORDS_FOUND, null, false, null);
            }

            return new MessageModel(MessageCatalog.RECORDS_FOUND, areas, false, null);

        }catch (Exception exception){
            return new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true, MessageCatalog.UNK_ERROR_FOUND.getMessage());
        }

    }

    @Override
    public AreaModel findById(long id) {
        Object findArea = repository.findById(id);
        AreaModel area = (AreaModel) findArea;
        return area;
    }

    @Override
    public MessageModel registerArea(AreaModel areaModel) {
        MessageModel messageModel;
        try {
            boolean isRegister = repository.saveAndFlush(areaModel) != null;
            if (isRegister) {
                messageModel = new MessageModel(MessageCatalog.SUCCESS_REGISTER, null, false, null);
            } else {
                messageModel = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true, MessageCatalog.UNK_ERROR_FOUND.getMessage());
            }
        } catch (DataAccessException exception) {
            messageModel = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true, exception.getMessage());
        }catch (Exception exception){
            messageModel = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true, exception.getMessage());
        }
        return messageModel;
    }

    @Override
    public boolean updateArea(AreaModel areaModel) {
        if (repository.existsById(areaModel.getId())) {
            return false;
        }
        return repository.saveAndFlush(areaModel) != null;
    }

    @Override
    public boolean disableArea(long id) {
        return false;
    }
}
