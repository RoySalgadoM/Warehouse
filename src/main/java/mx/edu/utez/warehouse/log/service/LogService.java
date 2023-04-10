package mx.edu.utez.warehouse.log.service;

import mx.edu.utez.warehouse.message.model.MessageModel;
import org.springframework.data.domain.Pageable;

public interface LogService {
    MessageModel findAllLogs(Pageable page, String username, String uuid);

    void saveLog(String action, Long idAction, String username, String uuid);
}
