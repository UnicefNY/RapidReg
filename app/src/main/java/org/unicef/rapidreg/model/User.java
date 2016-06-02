package org.unicef.rapidreg.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.unicef.rapidreg.db.PrimeroDB;
import org.unicef.rapidreg.utils.EncryptHelper;

@Table(database = PrimeroDB.class)
public class User extends BaseModel {

    @PrimaryKey(autoincrement = true)
    @Column
    int id;

    @Expose
    @SerializedName("user_name")
    @Column(name = "user_name")
    @Unique
    protected String username;

    @Column(name = "user_password")
    protected String password;

    @Expose
    @SerializedName("verified")
    @Column(name = "verified")
    protected boolean verified;

    @Expose
    @SerializedName("server_url")
    @Column(name = "server_url")
    protected String serverUrl;

    @Expose
    @SerializedName("db_key")
    @Column(name = "db_key")
    protected String dbKey;

    @Expose
    @SerializedName("organisation")
    @Column(name = "organisation")
    protected String organisation;

    @Expose
    @SerializedName("full_name")
    @Column(name = "full_name")
    protected String fullName;

    @Expose
    @SerializedName("unauthenticated_password")
    @Column(name = "unauthenticated_password")
    protected String unauthenticatedPassword;

    @Expose
    @SerializedName("language")
    @Column(name = "language")
    protected String language;

    public User() {
    }

    public User(String username) {
        this(username, null, false, null, null, null, null, null, null);
    }

    public User(String username, String password) {
        this(username, password, false, null, null, null, null, null, null);
    }

    public User(String username, String password, boolean verified) {
        this(username, password, verified, null, null, null, null, null, null);
    }

    public User(String username, String password, boolean verified, String serverUrl) {
        this(username, password, verified, serverUrl, null, null, null, null, null);
    }

    public User(String username, String password, boolean verified, String serverUrl,
                String dbKey, String organisation, String fullName, String unauthenticatedPassword,
                String language) {
        this.username = username;
        this.password = EncryptHelper.encrypt(password);
        this.verified = verified;
        this.serverUrl = serverUrl;
        this.dbKey = dbKey;
        this.organisation = organisation;
        this.fullName = fullName;
        this.unauthenticatedPassword = unauthenticatedPassword;
        this.language = language;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = EncryptHelper.encrypt(password);
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getDbKey() {
        return dbKey;
    }

    public void setDbKey(String dbKey) {
        this.dbKey = dbKey;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUnauthenticatedPassword() {
        return unauthenticatedPassword;
    }

    public void setUnauthenticatedPassword(String unauthenticatedPassword) {
        this.unauthenticatedPassword = unauthenticatedPassword;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void updateFields(User user) {
        this.password = user.getPassword();
        this.verified = user.isVerified();
        this.serverUrl = user.getServerUrl();
        this.dbKey = user.getDbKey();
        this.language = user.getLanguage();
        this.organisation = user.getOrganisation();
        this.fullName = user.getFullName();
        this.unauthenticatedPassword = user.getUnauthenticatedPassword();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<User>").append("\n");
        sb.append("name:").append(username).append("\n");
        sb.append("password:").append(password).append("\n");
        sb.append("verified:").append(verified).append("\n");
        sb.append("serverUrl:").append(serverUrl).append("\n");
        sb.append("dbKey:").append(dbKey).append("\n");
        sb.append("language:").append(language).append("\n");

        return sb.toString();
    }
}
