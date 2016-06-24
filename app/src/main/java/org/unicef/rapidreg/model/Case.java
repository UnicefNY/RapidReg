package org.unicef.rapidreg.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.data.Blob;
import com.raizlabs.android.dbflow.sql.language.Condition;
import com.raizlabs.android.dbflow.sql.language.NameAlias;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.unicef.rapidreg.db.PrimeroDB;

import java.sql.Date;
import java.util.List;

@Table(database = PrimeroDB.class)
public class Case extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    long id;
    @Column(name = "name")
    private String name;
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
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "created_at")
    private Date createAt;
    @Column(name = "last_updated_at")
    private Date lastUpdatedAt;
    @Column(name = "last_synced_at")
    private Date lastSyncedAt;
    @Column(name = "age")
    private int age;

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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Date getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(Date lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public Date getLastSyncedAt() {
        return lastSyncedAt;
    }

    public void setLastSyncedAt(Date lastSyncedAt) {
        this.lastSyncedAt = lastSyncedAt;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Case{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", content=" + content +
                ", photo=" + photo +
                ", audio=" + audio +
                ", isSynced=" + isSynced +
                ", syncLog='" + syncLog + '\'' +
                ", internalId='" + internalId + '\'' +
                ", internalRev='" + internalRev + '\'' +
                ", uniqueId='" + uniqueId + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createAt=" + createAt +
                ", lastUpdatedAt=" + lastUpdatedAt +
                ", lastSyncedAt=" + lastSyncedAt +
                ", age=" + age +
                '}';
    }
}

