package org.unicef.rapidreg;

public class PrimeroConfiguration {
    public static final float PHOTO_MAX_WIDTH = 1080;
    public static final float PHOTO_MAX_HEIGHT = 1920;
    public static final int PHOTO_MAX_SIZE_KB = 1000;
    public static final int PHOTO_THUMBNAIL_SIZE = 210;
    //    public static final String API_BASE_URL = "http://10.29.3.184:3000";
    private static String apiBaseUrl = "https://10.29.3.184:8443";

    private static String cookie = null;

    private static String internalFilePath = null;

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


    public static String getInternalFilePath() {
        return internalFilePath;
    }

    public static void setInternalFilePath(String internalFilePath) {
        PrimeroConfiguration.internalFilePath = internalFilePath;
    }
}
