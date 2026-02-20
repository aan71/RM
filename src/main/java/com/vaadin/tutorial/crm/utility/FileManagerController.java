package com.vaadin.tutorial.crm.utility;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import com.vaadin.tutorial.crm.backend.entity.Audit;
import com.vaadin.tutorial.crm.backend.service.AuditService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FileManagerController {
    private ApplicationContext ctx;
    private AuditService auditService;
    private Audit audit;

    @Autowired
    public void configure(ApplicationContext ctx, AuditService auditService) {
        this.ctx = ctx;
        this.auditService = auditService;
    }

    @RequestMapping(value={"/downloadExcelFile/file"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public ResponseEntity<byte[]> downloadExcelFile(@RequestParam("sPath") String sPath, @RequestParam("sId") String sId)
    {
        ResponseEntity<byte[]> responseEntity = null;
        try
        {
            Resource resource = this.ctx.getResource("file:"+sPath);

            if(resource.exists()){
                File file = File.createTempFile("TMP", ".xlsx");

                final InputStream is = resource.getInputStream();
                try {
                    final InputStreamReader reader = new InputStreamReader(is);
                    try {
                        FileUtils.copyInputStreamToFile(is, file);
                    } finally {
                        reader.close();
                    }
                } finally {
                    is.close();
                }

                if (!sId.equals("-1")){
                    Calendar calendar = Calendar.getInstance();
                    Date now = calendar.getTime();
                    Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());

                    audit=new Audit();
                    audit.setOperation("Download");
                    audit.setEntry_date(currentTimestamp);
                    audit.setRecord_id(Long.parseLong(sId));
                    audit.setDocument_format("xlsx");
                    audit.setUser_name(SecurityContextHolder.getContext().getAuthentication().getName());
                    auditService.save(audit);
                }
                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.set("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                responseHeaders.set("Content-Length", Long.toString(file.length()));
                responseHeaders.set("Content-Disposition", "attachment; filename=" + URLEncoder.encode(resource.getFilename(), "UTF8"));

                responseEntity = new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), responseHeaders, HttpStatus.OK);

                FileUtils.forceDelete(file);
            }else{
                responseEntity = null;
            }

        }
        catch (Exception localException) {

        }
        return responseEntity;
    }

    @RequestMapping(value={"/downloadPdfFile/file"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public ResponseEntity<byte[]> downloadPdfFile(@RequestParam("sPath") String sPath, @RequestParam("sId") String sId)
    {
        ResponseEntity<byte[]> responseEntity = null;
        try
        {
            Resource resource = this.ctx.getResource("file:"+sPath);

            if(resource.exists()){
                File file = File.createTempFile("TMP", ".pdf");

                final InputStream is = resource.getInputStream();
                try {
                    final InputStreamReader reader = new InputStreamReader(is);
                    try {
                        FileUtils.copyInputStreamToFile(is, file);
                    } finally {
                        reader.close();
                    }
                } finally {
                    is.close();
                }

                if (!sId.equals("-1")){
                    Calendar calendar = Calendar.getInstance();
                    Date now = calendar.getTime();
                    Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());

                    audit=new Audit();
                    audit.setOperation("Download");
                    audit.setEntry_date(currentTimestamp);
                    audit.setRecord_id(Long.parseLong(sId));
                    audit.setDocument_format("pdf");
                    audit.setUser_name(SecurityContextHolder.getContext().getAuthentication().getName());
                    auditService.save(audit);
                }
                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.set("Content-Type", "application/pdf");
                responseHeaders.set("Content-Length", Long.toString(file.length()));
                responseHeaders.set("Content-Disposition", "attachment; filename=" + URLEncoder.encode(resource.getFilename(), "UTF8"));

                responseEntity = new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), responseHeaders, HttpStatus.OK);

                FileUtils.forceDelete(file);
            }else{
                responseEntity = null;
            }

        }
        catch (Exception localException) {

        }
        return responseEntity;
    }

    @RequestMapping(value={"/downloadTxtFile/file"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public ResponseEntity<byte[]> downloadTxtFile(@RequestParam("sPath") String sPath, @RequestParam("sId") String sId)
    {
        ResponseEntity<byte[]> responseEntity = null;
        try
        {
            Resource resource = this.ctx.getResource("file:"+sPath);

            if(resource.exists()){
                File file = File.createTempFile("TMP", ".txt");

                final InputStream is = resource.getInputStream();
                try {
                    final InputStreamReader reader = new InputStreamReader(is);
                    try {
                        FileUtils.copyInputStreamToFile(is, file);
                    } finally {
                        reader.close();
                    }
                } finally {
                    is.close();
                }

                if (!sId.equals("-1")){
                    Calendar calendar = Calendar.getInstance();
                    Date now = calendar.getTime();
                    Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());

                    audit=new Audit();
                    audit.setOperation("Download");
                    audit.setEntry_date(currentTimestamp);
                    audit.setRecord_id(Long.parseLong(sId));
                    audit.setDocument_format("txt");
                    audit.setUser_name(SecurityContextHolder.getContext().getAuthentication().getName());
                    auditService.save(audit);
                }
                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.set("Content-Type", "text/plain");
                responseHeaders.set("Content-Length", Long.toString(file.length()));
                responseHeaders.set("Content-Disposition", "attachment; filename=" + URLEncoder.encode(resource.getFilename(), "UTF8"));

                responseEntity = new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), responseHeaders, HttpStatus.OK);

                FileUtils.forceDelete(file);
            }else{
                responseEntity = null;
            }

        }
        catch (Exception localException) {

        }
        return responseEntity;
    }

}
