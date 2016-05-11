package org.unicef.rapidreg.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @Expose @SerializedName("user_name") protected String userName;
    protected String password;
    @Expose @SerializedName("verified") protected boolean verified;
    @Expose @SerializedName("server_url") protected String serverUrl;
    @Expose @SerializedName("db_key") protected String dbKey;
    @Expose @SerializedName("organisation") protected String organisation;
    @Expose @SerializedName("full_name") protected String fullName;
    @Expose @SerializedName("unauthenticated_password") protected String unauthenticatedPassword;
    @Expose @SerializedName("language") protected String language;

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
        this.password = password;
        this.verified = verified;
        this.serverUrl = serverUrl;
        this.dbKey = dbKey;
        this.organisation = organisation;
        this.fullName = fullName;
        this.unauthenticatedPassword = unauthenticatedPassword;
        this.language = language;
    }
}
