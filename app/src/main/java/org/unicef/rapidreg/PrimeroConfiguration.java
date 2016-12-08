package org.unicef.rapidreg;

import org.unicef.rapidreg.model.User;

public class PrimeroConfiguration {
    //    public static final String API_BASE_URL = "http://10.29.3.184:3000";
    private static String apiBaseUrl = "https://10.29.3.184:8443";

    private static String cookie = null;

    private static String internalFilePath = null;

    private static User currentUser;

    public static String getApiBaseUrl() {
        return apiBaseUrl;
    }

    public static void setApiBaseUrl(String apiBaseUrl) {
        PrimeroConfiguration.apiBaseUrl = apiBaseUrl;
    }

    public static String getCookie() {
        return cookie;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        PrimeroConfiguration.currentUser = currentUser;
    }

    public static void setCookie(String cookie) {
        PrimeroConfiguration.cookie = cookie;
    }


    public static String getInternalFilePath() {
        return internalFilePath;
    }

    public static void setInternalFilePath(String internalFilePath) {
        PrimeroConfiguration.internalFilePath = internalFilePath;
    }
}
