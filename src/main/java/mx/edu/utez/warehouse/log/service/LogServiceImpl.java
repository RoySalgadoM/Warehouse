package mx.edu.utez.warehouse.log.service;

import mx.edu.utez.warehouse.area.model.AreaModel;
import mx.edu.utez.warehouse.log.model.LogModel;
import mx.edu.utez.warehouse.message.model.MessageModel;
import mx.edu.utez.warehouse.utils.MessageCatalog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Date;

@Service
public class LogServiceImpl implements LogService{
    private static final Logger logger = LogManager.getLogger(LogServiceImpl.class);

    @Autowired
    private LogRepository logRepository;

    @Override
    public MessageModel findAllLogs(Pageable page, String username, String uuid) {
        try {
            Page<LogModel> areas = logRepository.findAll(page);

            if (areas.getNumberOfElements() == 0) {
                return new MessageModel(MessageCatalog.NO_RECORDS_FOUND, null, false);
            }

            return new MessageModel(MessageCatalog.RECORDS_FOUND, areas, false);

        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> LOG MODULE ---> findAllLogs() ERROR: {}", username, uuid, exception.getMessage());
            return new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
    }

    @Override
    public void saveLog(String action, Long idAction, String username, String uuid) {
        try {
            LogModel log = new LogModel(username, uuid, action, idAction, new Date(System.currentTimeMillis()));
            logRepository.save(log);
        }catch (Exception exception){
            logger.error("[USER : {}] || [UUID : {}] ---> LOG MODULE ---> saveLog() ERROR: {}", username, uuid, exception.getMessage());
        }
    }
}
