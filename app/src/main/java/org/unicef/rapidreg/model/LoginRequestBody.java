package org.unicef.rapidreg.model;

import com.google.gson.annotations.SerializedName;

public class LoginRequestBody {
    @SerializedName("user_name")
    String userName;
    @SerializedName("password")
    String password;
    @SerializedName("mobile_number")
    String mobileNumber;
    @SerializedName("imei")
    String imei;

    public LoginRequestBody() {
    }

    public LoginRequestBody(String userName, String password, String mobileNumber, String imei) {
        this.userName = userName;
        this.password = password;
        this.mobileNumber = mobileNumber;
        this.imei = imei;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }
}
