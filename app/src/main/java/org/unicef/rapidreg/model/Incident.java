package org.unicef.rapidreg.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import org.unicef.rapidreg.PrimeroDatabaseConfiguration;

import static org.unicef.rapidreg.model.Incident.COLUMN_INCIDENT_CASE_ID;

@Table(database = PrimeroDatabaseConfiguration.class)
public class Incident extends RecordModel {

    public static final String COLUMN_SURVIVOR_CODE = "survivor_code";
    public static final String COLUMN_TYPE_OF_VIOLENCE = "type_of_violence";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_CASE_UNIQUE_ID = "case_unique_id";
    public static final String COLUMN_INCIDENT_CASE_ID = "incident_case_id";

    @Column(name = COLUMN_SURVIVOR_CODE)
    private String survivorCode;

    @Column(name = COLUMN_TYPE_OF_VIOLENCE)
    private String typeOfViolence;

    @Column(name = COLUMN_LOCATION)
    private String location;

    @Column(name = COLUMN_CASE_UNIQUE_ID)
    private String caseUniqueId;

    @Column(name = COLUMN_INCIDENT_CASE_ID)
    private String incidentCaseId;

    public Incident() {
    }

    public Incident(long id) {
        super(id);
    }

    public String getSurvivorCode() {
        return survivorCode;
    }

    public void setSurvivorCode(String survivorCode) {
        this.survivorCode = survivorCode;
    }

    public String getTypeOfViolence() {
        return typeOfViolence;
    }

    public void setTypeOfViolence(String typeOfViolence) {
        this.typeOfViolence = typeOfViolence;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setCaseUniqueId(String caseUniqueId) {
        this.caseUniqueId = caseUniqueId;
    }

    public String getCaseUniqueId() {
        return caseUniqueId;
    }

    public String getIncidentCaseId() {
        return incidentCaseId;
    }

    public void setIncidentCaseId(String incidentCaseId) {
        this.incidentCaseId = incidentCaseId;
    }
}
