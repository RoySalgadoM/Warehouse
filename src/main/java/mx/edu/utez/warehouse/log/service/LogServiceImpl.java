package mx.edu.utez.warehouse.log.service;

import mx.edu.utez.warehouse.log.model.LogModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;

@Service
public class LogServiceImpl implements LogService{
    private static final Logger logger = LogManager.getLogger(LogServiceImpl.class);

    @Autowired
    private LogRepository logRepository;
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
