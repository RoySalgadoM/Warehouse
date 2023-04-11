package mx.edu.utez.warehouse.log.controller;

import jakarta.servlet.http.HttpSession;
import mx.edu.utez.warehouse.area.model.AreaModel;
import mx.edu.utez.warehouse.log.service.LogServiceImpl;
import mx.edu.utez.warehouse.message.model.MessageModel;
import mx.edu.utez.warehouse.security.config.SecurityUser;
import mx.edu.utez.warehouse.utils.MessageCatalog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/log")
public class LogController {
    private static final Logger logger = LogManager.getLogger(LogController.class);
    private static final String SPRING_SECURITY_CONTEXT = "SPRING_SECURITY_CONTEXT";
    private static final String RESULT = "result";
    private static final String PAGE_SIZE = "pageSize";
    private static final String ERROR_500 = "errorPages/500";
    private static final String LOG_REDIRECT = "log/log";
    @Autowired
    LogServiceImpl logService;

    @GetMapping("/list")
    public String findAllLogs(Model model, HttpSession httpSession, Pageable pageable, @ModelAttribute("area") AreaModel area) {
        UUID uuid = UUID.randomUUID();
        String username = "";
        try {
            SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute(SPRING_SECURITY_CONTEXT);
            SecurityUser user = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user.getUsername();
            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING LOG MODULE ---> findAllLogs()", username, uuid);
            MessageModel logs = logService.findAllLogs(pageable, username, uuid.toString());
            logger.info("[USER : {}] || [UUID : {}] ---> LOG MODULE ---> findAllLogs() RESPONSE: {}", username, uuid, logs.getMessage());
            logs.setUuid(uuid.toString());

            model.addAttribute(RESULT, logs);
            if (logs.getIsError()) {
                return ERROR_500;
            }
            model.addAttribute(PAGE_SIZE, pageable.getPageSize());
            logService.saveLog("FINDING LOGS", 0L, username, uuid.toString());
            return LOG_REDIRECT;
        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> LOG MODULE ---> findAllLogs() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            model.addAttribute(RESULT, message);
            return ERROR_500;
        }
    }
}
