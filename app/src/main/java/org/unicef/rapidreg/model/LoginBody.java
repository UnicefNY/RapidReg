package org.unicef.rapidreg.model;

import com.google.gson.annotations.SerializedName;

public class LoginBody {
    @SerializedName("mobile_number") String mobileNumber;
    @SerializedName("password") String password;
    @SerializedName("user_name") String userName;
    @SerializedName("imei") String imei;

    public LoginBody() {
    }

    public LoginBody(String mobileNumber, String password, String userName, String imei) {
        this.mobileNumber = mobileNumber;
        this.password = password;
        this.userName = userName;
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
