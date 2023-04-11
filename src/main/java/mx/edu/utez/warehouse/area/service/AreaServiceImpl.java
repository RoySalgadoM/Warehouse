package mx.edu.utez.warehouse.area.service;

import jakarta.persistence.NoResultException;
import mx.edu.utez.warehouse.area.model.AreaModel;
import mx.edu.utez.warehouse.message.model.MessageModel;
import mx.edu.utez.warehouse.utils.MessageCatalog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AreaServiceImpl implements AreaService {
    private static final Logger logger = LogManager.getLogger(AreaServiceImpl.class);
    private static final String AREA_NOT_FOUND = "The area could not be found";
    @Autowired
    AreaRepository repository;

    @Override
    public List<AreaModel> findAreas() {
        return repository.findAll();
    }

    @Override
    public MessageModel findAllAreas(Pageable page, String username, String uuid) {
        try {
            Page<AreaModel> areas = repository.findAll(page);

            if (areas.getNumberOfElements() == 0) {
                return new MessageModel(MessageCatalog.NO_RECORDS_FOUND, null, false);
            }

            return new MessageModel(MessageCatalog.RECORDS_FOUND, areas, false);

        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> AREA MODULE ---> findAllAreas() ERROR: {}", username, uuid, exception.getMessage());
            return new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }

    }

    @Override
    public MessageModel findById(long id, String username, String uuid) {
        try {
            var findArea = repository.findById(id);
            if (findArea.isEmpty()) {
                throw new NoResultException(AREA_NOT_FOUND);
            }
            return new MessageModel(MessageCatalog.RECORDS_FOUND, findArea.get(), false);

        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> AREA MODULE ---> findById() ERROR: {}", username, uuid, exception.getMessage());
            return new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
    }

    @Override
    public MessageModel registerArea(AreaModel areaModel, String username, String uuid) {
        MessageModel messageModel;
        try {
            areaModel.setStatus(1);
            repository.save(areaModel);
            messageModel = new MessageModel(MessageCatalog.SUCCESS_REGISTER, null, false);

        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> AREA MODULE ---> saveArea() ERROR: {}", username, uuid, exception.getMessage());
            messageModel = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
        return messageModel;
    }

    @Override
    public MessageModel updateArea(AreaModel areaModel, String username, String uuid) {
        try {
            var area = repository.findById(areaModel.getId());
            if (area.isEmpty()) {
                throw new NoResultException(AREA_NOT_FOUND);
            }
            area.get().setAddress(areaModel.getAddress());
            area.get().setIdentifier(areaModel.getIdentifier());
            repository.saveAndFlush(area.get());
            return new MessageModel(MessageCatalog.SUCCESS_UPDATE, null, false);

        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> AREA MODULE ---> updateArea() ERROR: {}", username, uuid, exception.getMessage());
            return new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
    }

    @Override
    public MessageModel disableArea(long id, String username, String uuid) {
        try {
            var area = repository.findById(id);
            if (area.isEmpty()) {
                throw new NoResultException(AREA_NOT_FOUND);
            }
            area.get().setStatus(area.get().getStatus() == 1 ? 0 : 1);
            repository.saveAndFlush(area.get());
            return new MessageModel(area.get().getStatus() == 1 ? MessageCatalog.SUCCESS_ENABLE : MessageCatalog.SUCCESS_DISABLE, null, false);

        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> AREA MODULE ---> disableArea() ERROR: {}", username, uuid, exception.getMessage());
            return new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
    }

    public boolean isExistArea(String identifier) {
        return repository.existsByIdentifier(identifier);
    }
    public boolean isExistAreaAndId(String identifier, Long id) {
        return repository.existsByIdentifierAndIdNotLike(identifier, id);
    }
    public boolean isExistById(Long id) {
        return repository.existsById(id);
    }
}
