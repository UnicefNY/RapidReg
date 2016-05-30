package org.unicef.rapidreg.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.mindrot.jbcrypt.BCrypt;
import org.unicef.rapidreg.db.PrimeroDB;

@Table(database = PrimeroDB.class)
public class User extends BaseModel {

    @PrimaryKey(autoincrement = true)
    @Column
    int id;

    @Expose
    @SerializedName("user_name")
    @Column
    protected String userName;
    @Column
    protected String password;
    @Expose
    @SerializedName("verified")
    @Column
    protected boolean verified;
    @Expose
    @SerializedName("server_url")
    @Column
    protected String serverUrl;
    @Expose
    @SerializedName("db_key")
    @Column
    protected String dbKey;
    @Expose
    @SerializedName("organisation")
    @Column
    protected String organisation;
    @Expose
    @SerializedName("full_name")
    @Column
    protected String fullName;
    @Expose
    @SerializedName("unauthenticated_password")
    @Column
    protected String unauthenticatedPassword;
    @Expose
    @SerializedName("language")
    @Column
    protected String language;

    public User() {
    }

    public User(String userName) {
        this(userName, null, false, null, null, null, null, null, null);
    }

    public User(String userName, String password) {
        this(userName, password, false, null, null, null, null, null, null);
    }

    public User(String userName, String password, boolean verified) {
        this(userName, password, verified, null, null, null, null, null, null);
    }

    public User(String userName, String password, boolean verified, String serverUrl) {
        this(userName, password, verified, serverUrl, null, null, null, null, null);
    }

    public User(String userName, String password, boolean verified, String serverUrl,
                String dbKey, String organisation, String fullName, String unauthenticatedPassword,
                String language) {
        this.userName = userName;
        this.password = encryptPassword(password);
        this.verified = verified;
        this.serverUrl = serverUrl;
        this.dbKey = dbKey;
        this.organisation = organisation;
        this.fullName = fullName;
        this.unauthenticatedPassword = unauthenticatedPassword;
        this.language = language;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = encryptPassword(password);
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

    private String encryptPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }
}
