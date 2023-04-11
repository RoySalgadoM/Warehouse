package mx.edu.utez.warehouse.output.service;

import mx.edu.utez.warehouse.message.model.MessageModel;
import mx.edu.utez.warehouse.output.model.OutputModel;
import org.springframework.data.domain.Pageable;

public interface OutputService {
    MessageModel findAllOutputs(Pageable page, String username, String uuid);
    MessageModel findById(long id, String username, String uuid);
    MessageModel registerOutput(OutputModel outputModel, String username, String uuid);
    MessageModel cancelOutput(long id, String username, String uuid);
    MessageModel sentOutput(long id, String username, String uuid);
}
