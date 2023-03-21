package mx.edu.utez.warehouse.area.service;

import mx.edu.utez.warehouse.area.model.AreaModel;
import mx.edu.utez.warehouse.message.model.MessageModel;

public interface AreaService {
    MessageModel findAllAreas(int page, int size, String username, String uuid);
    MessageModel findById(long id, String username, String uuid);
    MessageModel registerArea(AreaModel areaModel, String username, String uuid);
    boolean updateArea(AreaModel areaModel, String username, String uuid);
    boolean disableArea(long id, String username, String uuid);


}
