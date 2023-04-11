package mx.edu.utez.warehouse.area.service;

import mx.edu.utez.warehouse.area.model.AreaModel;
import mx.edu.utez.warehouse.message.model.MessageModel;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AreaService {
    List<AreaModel> findAreas();
    MessageModel findAllAreas(Pageable page, String username, String uuid);
    MessageModel findById(long id, String username, String uuid);
    MessageModel registerArea(AreaModel areaModel, String username, String uuid);
    MessageModel updateArea(AreaModel areaModel, String username, String uuid);
    MessageModel disableArea(long id, String username, String uuid);
}
