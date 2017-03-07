package org.unicef.rapidreg.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.unicef.rapidreg.PrimeroDatabaseConfiguration;

@Table(database = PrimeroDatabaseConfiguration.class)
public class SystemSettings extends BaseModel{
    @PrimaryKey(autoincrement = true)
    private long id;

    @Column(name = "server_url")
    @Unique
    private String serverUrl;

    @Column(name = "district_level")
    private int districtLevel;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public int getDistrictLevel() {
        return districtLevel;
    }

    public void setDistrictLevel(int districtLevel) {
        this.districtLevel = districtLevel;
    }

    @Override
    public String toString() {
        return "SystemSettings{" +
                "id=" + id +
                ", serverUrl='" + serverUrl + '\'' +
                ", districtLevel=" + districtLevel +
                '}';
    }
}
