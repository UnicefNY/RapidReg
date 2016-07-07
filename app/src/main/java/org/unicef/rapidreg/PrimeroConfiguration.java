package org.unicef.rapidreg;

public class PrimeroConfiguration {
    //    public static final String API_BASE_URL = "http://10.29.3.184:3000";
    private static String apiBaseUrl = "https://10.29.3.184:8443";

    private static String cookie = null;

    public static String getApiBaseUrl() {
        return apiBaseUrl;
    }

    public static void setApiBaseUrl(String apiBaseUrl) {
        PrimeroConfiguration.apiBaseUrl = apiBaseUrl;
    }

    public static String getCookie() {
        return cookie;
    }

    public static void setCookie(String cookie) {
        PrimeroConfiguration.cookie = cookie;
    }
}
