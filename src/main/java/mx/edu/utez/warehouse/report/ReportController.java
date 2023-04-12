package mx.edu.utez.warehouse.report;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mx.edu.utez.warehouse.entry.service.EntryServiceImpl;
import mx.edu.utez.warehouse.log.service.LogServiceImpl;
import mx.edu.utez.warehouse.output.service.OutputServiceImpl;
import mx.edu.utez.warehouse.security.config.SecurityUser;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRSaver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("report")
public class ReportController {
    private static final String TYPE_APP = "application/x-pdf";
    private static final String ATTACHMENT = "attachment; filename=entry.pdf";
    private static final Logger logger = LogManager.getLogger(ReportController.class);
    private static final String SPRING_SECURITY_CONTEXT = "SPRING_SECURITY_CONTEXT";
    @Autowired
    LogServiceImpl logService;
    @Autowired
    private Environment environment;
    @Autowired
    private EntryServiceImpl entryService;
    @Autowired
    private OutputServiceImpl outputService;

    @GetMapping("/entry/{id}")
    public JasperPrint entryReport(HttpServletResponse response, @PathVariable("id") int idEntry, HttpSession httpSession, Model model) {
        UUID uuid = UUID.randomUUID();
        String username = "";
        try{
            SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute(SPRING_SECURITY_CONTEXT);
            SecurityUser user = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user.getUsername();
            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING REPORT MODULE ---> entryReport()", username, uuid);
            InputStream inputStream = this.getClass().getResourceAsStream("/reports/entry.jrxml");
            response.setContentType(TYPE_APP);
            response.setHeader("Content-disposition", ATTACHMENT);
            JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
            JRSaver.saveObject(jasperReport, "entry.pdf");
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("ID_ENTRY", idEntry);
            String url = environment.getProperty("spring.datasource.url");
            String userDb = environment.getProperty("spring.datasource.username");
            String password = environment.getProperty("spring.datasource.password");
            String finalUrl = url + "?user=" + userDb + "&password=" + password;
            JasperPrint jasperPrint
                    = JasperFillManager.fillReport(jasperReport, parameters, DriverManager.getConnection(finalUrl));
            OutputStream outputStream = response.getOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
            logService.saveLog("CREATING ENTRY REPORT", (long) idEntry, username, uuid.toString());
        }catch (Exception exception){
            logger.error("[USER : {}] || [UUID : {}] ---> REPORT MODULE ---> entryReport() ERROR: {}", username, uuid, exception.getMessage());
        }
        return null;
    }

    @GetMapping("/output/{id}")
    public JasperPrint outputReport(HttpServletResponse response, @PathVariable("id") int idOutput, HttpSession httpSession){
        UUID uuid = UUID.randomUUID();
        String username = "";
        try{
            SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute(SPRING_SECURITY_CONTEXT);
            SecurityUser user = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user.getUsername();
            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING REPORT MODULE ---> outputReport()", username, uuid);
            InputStream inputStream = this.getClass().getResourceAsStream("/reports/output.jrxml");
            response.setContentType(TYPE_APP);
            response.setHeader("Content-disposition", "attachment; filename=output.pdf");
            JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
            JRSaver.saveObject(jasperReport, "entry.pdf");
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("ID_OUTPUT", idOutput);

            String url = environment.getProperty("spring.datasource.url");
            String userDb = environment.getProperty("spring.datasource.username");
            String password = environment.getProperty("spring.datasource.password");
            String finalUrl = url + "?user=" + userDb + "&password=" + password;
            JasperPrint jasperPrint
                    = JasperFillManager.fillReport(jasperReport, parameters, DriverManager.getConnection(finalUrl));
            OutputStream outputStream = response.getOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
            logService.saveLog("CREATING OUTPUT REPORT", (long) idOutput, username, uuid.toString());
        }catch (Exception exception){
            logger.error("[USER : {}] || [UUID : {}] ---> REPORT MODULE ---> outputReport() ERROR: {}", username, uuid, exception.getMessage());
        }
        return null;
    }
}
