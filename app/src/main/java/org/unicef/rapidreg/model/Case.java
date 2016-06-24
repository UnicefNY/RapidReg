package org.unicef.rapidreg.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.data.Blob;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.unicef.rapidreg.db.PrimeroDB;

import java.sql.Date;

@Table(database = PrimeroDB.class)
public class Case extends BaseModel {
    public static final int MIN_AGE = 0;
    public static final int MAX_AGE = 100;
    public static final String COLUMN_UNIQUE_ID = "unique_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_AGE = "age";
    public static final String COLUMN_CAREGIVER = "caregiver";
    public static final String COLUMN_REGISTRATION_DATE = "registration_date";

    @PrimaryKey(autoincrement = true)
    public long id;
    @Column(name = "name", defaultValue = "")
    private String name = "";
    @Column(name = "age")
    private int age;
    @Column(name = "caregiver", defaultValue = "")
    private String caregiver = "";
    @Column(name = "case_json")
    private Blob content;
    @Column(name = "photo")
    private Blob photo;
    @Column(name = "audio")
    private Blob audio;
    @Column(name = "is_synced")
    private boolean isSynced;
    @Column(name = "sync_log")
    private String syncLog;
    @Column(name = "_id")
    private String internalId;
    @Column(name = "_rev")
    private String internalRev;
    @Column(name = "unique_id")
    private String uniqueId;
    @Column(name = "registration_date")
    private Date registrationDate;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "created_date")
    private Date createDate;
    @Column(name = "last_updated_date")
    private Date lastUpdatedDate;
    @Column(name = "last_synced_date")
    private Date lastSyncedDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getCaregiver() {
        return caregiver;
    }

    public void setCaregiver(String caregiver) {
        this.caregiver = caregiver;
    }

    public Blob getContent() {
        return content;
    }

    public void setContent(Blob content) {
        this.content = content;
    }

    public Blob getPhoto() {
        return photo;
    }

    public void setPhoto(Blob photo) {
        this.photo = photo;
    }

    public Blob getAudio() {
        return audio;
    }

    public void setAudio(Blob audio) {
        this.audio = audio;
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }

    public String getSyncLog() {
        return syncLog;
    }

    public void setSyncLog(String syncLog) {
        this.syncLog = syncLog;
    }

    public String getInternalId() {
        return internalId;
    }

    public void setInternalId(String internalId) {
        this.internalId = internalId;
    }

    public String getInternalRev() {
        return internalRev;
    }

    public void setInternalRev(String internalRev) {
        this.internalRev = internalRev;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public Date getLastSyncedDate() {
        return lastSyncedDate;
    }

    public void setLastSyncedDate(Date lastSyncedDate) {
        this.lastSyncedDate = lastSyncedDate;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Case:");
        sb.append("id").append(id).append("\n");
        sb.append("name").append(name).append("\n");
        sb.append("age").append(age).append("\n");
        sb.append("caregiver").append(caregiver).append("\n");
        sb.append("content").append(content).append("\n");
        sb.append("isSynced").append(isSynced).append("\n");
        sb.append("syncLog").append(syncLog).append("\n");
        sb.append("internalId").append(internalId).append("\n");
        sb.append("internalRev").append(internalRev).append("\n");
        sb.append("uniqueId").append(uniqueId).append("\n");
        sb.append("registrationDate").append(registrationDate).append("\n");
        sb.append("createdBy").append(createdBy).append("\n");
        sb.append("createDate").append(createDate).append("\n");
        sb.append("lastUpdatedDate").append(lastUpdatedDate).append("\n");
        sb.append("lastSyncedDate").append(lastSyncedDate).append("\n");

        return sb.toString();
    }
}
