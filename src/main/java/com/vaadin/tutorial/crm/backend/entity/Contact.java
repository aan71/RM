package com.vaadin.tutorial.crm.backend.entity;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name="RM_REPORT_ATTRIBUTES")
public class Contact extends AbstractEntity implements Cloneable {

    @NotNull
    @NotEmpty
	private String system_source = "";

    @NotNull
    @NotEmpty
	private String broker_identifier = "";

    @NotNull
    @NotEmpty
	private String reinsurer_identifier = "";

    @NotNull
    @NotEmpty
	private String document_type = "";

	private String document_name = "";

	private String document_format = "";

	private String document_path = "";

    @Temporal(TemporalType.TIMESTAMP)
	private Date document_reference_date;

    @Temporal(TemporalType.TIMESTAMP)
	private Date document_creation_date;

    private String user_name = "";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_status", referencedColumnName="id")
    @Fetch(FetchMode.JOIN)
    private com.vaadin.tutorial.crm.backend.entity.Status status;

    public String getSystem_source() { return system_source; }
    public void setSystem_source(String system_source) { this.system_source = system_source; }

    public String getBroker_identifier() { return broker_identifier; }
    public void setBroker_identifier(String broker_identifier) { this.broker_identifier = broker_identifier; }

    public String getReinsurer_identifier() { return reinsurer_identifier; }
    public void setReinsurer_identifier(String reinsurer_identifier) { this.reinsurer_identifier = reinsurer_identifier; }

    public String getDocument_type() { return document_type; }
    public void setDocument_type(String document_type) { this.document_type = document_type; }

    public String getDocument_name() { return document_name; }
    public void setDocument_name(String document_name) { this.document_name = document_name; }

    public String getDocument_format() { return document_format; }
    public void setDocument_format(String document_format) { this.document_format = document_format; }

    public String getDocument_path() { return document_path; }
    public void setDocument_path(String document_path) { this.document_path = document_path; }

    public Date getDocument_reference_date() { return document_reference_date; }
    public void setDocument_reference_date(Date document_reference_date) { this.document_reference_date = document_reference_date; }

    public Date getDocument_creation_date() { return document_creation_date; }
    public void setDocument_creation_date(Date document_creation_date) { this.document_creation_date = document_creation_date; }

    public String getUser_name() { return user_name; }
    public void setUser_name(String user_name) { this.user_name = user_name; }

    public void setStatus(Status status) { this.status = status; }
    public Status getStatus() { return status; }

    public String getFormattedDate(String formatDate) {
        DateFormat dateFormat = new SimpleDateFormat(formatDate);
        String strDate = dateFormat.format(this.document_creation_date);
        return strDate;
    }

}
