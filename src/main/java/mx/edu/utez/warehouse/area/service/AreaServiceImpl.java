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
    public MessageModel findAllAreas(Pageable page, String username, String uuid) {
        try{
            Page<AreaModel> areas = repository.findAll(page);


            if(areas.getNumberOfElements() == 0){
                return new MessageModel(MessageCatalog.NO_RECORDS_FOUND, null, false);
            }

            return new MessageModel(MessageCatalog.RECORDS_FOUND, areas, false);

        }catch (DataAccessException exception) {
            logger.error("[USER : " + username + "] || [UUID : " + uuid + "] ---> AREA MODULE --> findAllAreas() ERROR: " + exception.getMessage());
            return new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
        catch (Exception exception){
            logger.error("[USER : " + username + "] || [UUID : " + uuid + "] ---> AREA MODULE --> findAllAreas() ERROR: " + exception.getMessage());
            return new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }

    }

    @Override
    public MessageModel findById(long id, String username, String uuid) {
        try {
            var findArea = repository.findById(id);
            if(findArea.isEmpty()){
                return new MessageModel(MessageCatalog.NO_RECORDS_FOUND, null, false);
            }
            return new MessageModel(MessageCatalog.RECORDS_FOUND, findArea.get(), false);

        }catch (DataAccessException exception) {
            logger.error("[USER : " + username + "] || [UUID : " + uuid + "] ---> AREA MODULE --> findById() ERROR: " + exception.getMessage());
            return new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }catch (Exception exception){
            logger.error("[USER : " + username + "] || [UUID : " + uuid + "] ---> AREA MODULE --> findById() ERROR: " + exception.getMessage());
            return new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
    }

    @Override
    public MessageModel registerArea(AreaModel areaModel, String username, String uuid) {
        MessageModel messageModel;
        try {
            boolean isRegister = repository.saveAndFlush(areaModel) != null;
            if (isRegister) {
                messageModel = new MessageModel(MessageCatalog.SUCCESS_REGISTER, null, false);
            } else {
                messageModel = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            }
        } catch (DataAccessException exception) {
            logger.error("[USER : " + username + "] || [UUID : " + uuid + "] ---> AREA MODULE --> findById() ERROR: " + exception.getMessage());
            messageModel = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }catch (Exception exception){
            logger.error("[USER : " + username + "] || [UUID : " + uuid + "] ---> AREA MODULE --> findById() ERROR: " + exception.getMessage());
            messageModel = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
        return messageModel;
    }

    @Override
    public boolean updateArea(AreaModel areaModel, String username, String uuid) {
        if (repository.existsById(areaModel.getId())) {
            return false;
        }
        return repository.saveAndFlush(areaModel) != null;
    }

    @Override
    public boolean disableArea(long id, String username, String uuid) {
        return false;
    }
}
