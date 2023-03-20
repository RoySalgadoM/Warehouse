package mx.edu.utez.warehouse.area.service;

import mx.edu.utez.warehouse.area.model.AreaModel;
import mx.edu.utez.warehouse.message.model.MessageModel;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AreaService {
    MessageModel findAllAreas(int page, int size);
    AreaModel findById(long id);
    MessageModel registerArea(AreaModel areaModel);
    boolean updateArea(AreaModel areaModel);
    boolean disableArea(long id);


}
