package mx.edu.utez.warehouse.log.service;

public interface LogService {
    void saveLog(String action, Long idAction, String username, String uuid);
}
