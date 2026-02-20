package com.vaadin.tutorial.crm.backend.entity;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Entity
@Table(name="RM_AUDIT")
public class Audit extends AbstractAuditEntity implements Cloneable {

    private String table_name = "";
    private String entity_name = "";
    private String new_value = "";
    private String old_value = "";
    private String user_name = "";
    private Timestamp entry_date;
    private String operation = "";
    private Long record_id;
    private String document_format = "";

    public String getTable_name() { return table_name; }
    public void setTable_name(String table_name) { this.table_name = table_name; }

    public String getEntity_name() { return entity_name; }
    public void setEntity_name(String entity_name) { this.entity_name = entity_name; }

    public String getNew_value() { return new_value; }
    public void setNew_value(String new_value) { this.new_value = new_value; }

    public String getOld_value() { return old_value; }
    public void setOld_value(String old_value) { this.old_value = old_value; }

    public String getUser_name() { return user_name; }
    public void setUser_name(String user_name) { this.user_name = user_name; }

    public Timestamp getEntry_date() { return entry_date; }
    public void setEntry_date(Timestamp entry_date) { this.entry_date = entry_date; }

    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }

    public Long getRecord_id() { return record_id; }
    public void setRecord_id(Long record_id) { this.record_id = record_id; }

    public String getDocument_format() { return document_format; }
    public void setDocument_format(String document_format) { this.document_format = document_format; }

    public String getFormattedDate(String formatDate) {
        DateFormat dateFormat = new SimpleDateFormat(formatDate);
        String strDate = dateFormat.format(this.entry_date);
        return strDate;
    }

}
