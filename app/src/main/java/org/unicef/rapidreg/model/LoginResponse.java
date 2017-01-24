package org.unicef.rapidreg.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LoginResponse {
    private Session session;

    @SerializedName("db_key")
    private String dbKey;

    private String organization;

    @SerializedName("module_ids")
    private List<String> moduleIds;

    private String language;

    private boolean verified;

    public void setSession(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return this.session;
    }

    public void setDbKey(String dbKey) {
        this.dbKey = dbKey;
    }

    public String getDbKey() {
        return this.dbKey;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getOrganization() {
        return this.organization;
    }

    public List<String> getModuleIds() {
        return moduleIds;
    }

    public void setModuleIds(List<String> moduleIds) {
        this.moduleIds = moduleIds;
    }

    public String getRole() {
        if (moduleIds == null || moduleIds.isEmpty()) {
            return null;
        }
        return moduleIds.get(0);
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return this.language;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean getVerified() {
        return this.verified;
    }

}
