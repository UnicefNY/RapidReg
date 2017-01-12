package org.unicef.rapidreg.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.data.Blob;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;


public class RecordModel extends BaseModel {
    public static final int CASE = 0;
    public static final int TRACING = 1;

    public static final int EMPTY_AGE = -1;
    public static final String COLUMN_UNIQUE_ID = "unique_id";
    public static final String COLUMN_SHORT_ID = "short_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_AGE = "age";
    public static final String COLUMN_CAREGIVER = "caregiver";
    public static final String COLUMN_REGISTRATION_DATE = "registration_date";
    public static final String COLUMN_CREATED_BY = "created_by";

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
    @Column(name = "short_id")
    private String shortId;
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
    @Column(name = "type")
    private int type;
    @Column(name = "module_id")
    private String moduleId;

    @Column
    private boolean isAudioSynced;

    public RecordModel(long id) {
        this.id = id;
    }

    public RecordModel() {
    }

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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isAudioSynced() {
        return isAudioSynced;
    }

    public void setAudioSynced(boolean audioSynced) {
        isAudioSynced = audioSynced;
    }

    public String getShortId() {
        return shortId;
    }

    public void setShortId(String shortId) {
        this.shortId = shortId;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    @Override
    public String toString() {
        return "RecordModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", caregiver='" + caregiver + '\'' +
                ", content=" + content +
                ", audio=" + audio +
                ", isSynced=" + isSynced +
                ", syncLog='" + syncLog + '\'' +
                ", internalId='" + internalId + '\'' +
                ", internalRev='" + internalRev + '\'' +
                ", uniqueId='" + uniqueId + '\'' +
                ", shortId='" + shortId + '\'' +
                ", registrationDate=" + registrationDate +
                ", createdBy='" + createdBy + '\'' +
                ", createDate=" + createDate +
                ", lastUpdatedDate=" + lastUpdatedDate +
                ", lastSyncedDate=" + lastSyncedDate +
                ", type=" + type +
                ", moduleId=" + moduleId +
                ", isAudioSynced=" + isAudioSynced +
                '}';
    }
}
