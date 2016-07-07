package org.unicef.rapidreg.widgets;

public class PrimeroConfiguration {
    //    public static final String API_BASE_URL = "http://10.29.3.184:3000";
    private static String apiBaseUrl = "https://10.29.3.184:8443";


    public static String getApiBaseUrl() {
        return apiBaseUrl;
    }

    public static void setApiBaseUrl(String apiBaseUrl) {
        PrimeroConfiguration.apiBaseUrl = apiBaseUrl;
    }
}
