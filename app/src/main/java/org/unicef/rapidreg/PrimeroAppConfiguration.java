package org.unicef.rapidreg;

import android.provider.Settings;

import org.unicef.rapidreg.model.User;
import org.unicef.rapidreg.utils.TextUtils;

import java.util.Locale;

public class PrimeroAppConfiguration {
    public static final String MODULE_ID_GBV = "primeromodule-gbv";
    public static final String MODULE_ID_CP = "primeromodule-cp";

    public static final String PARENT_CASE = "case";
    public static final String PARENT_INCIDENT = "incident";
    public static final String PARENT_TRACING_REQUEST = "tracing_request";

    private static String apiBaseUrl = "https://127.0.0.1:8443";

    private static String cookie = null;

    private static String internalFilePath = null;

    private static User currentUser;

    public static String getApiBaseUrl() {
        return apiBaseUrl;
    }

    public static void setApiBaseUrl(String apiBaseUrl) {
        apiBaseUrl = TextUtils.lintUrl(apiBaseUrl);
        PrimeroAppConfiguration.apiBaseUrl = apiBaseUrl;
    }

    public static String getCookie() {
        return cookie;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static String getCurrentUsername() {
        return currentUser.getUsername();
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static void setCookie(String cookie) {
        PrimeroAppConfiguration.cookie = cookie;
    }

    public static String getInternalFilePath() {
        return internalFilePath;
    }

    public static void setInternalFilePath(String internalFilePath) {
        PrimeroAppConfiguration.internalFilePath = internalFilePath;
    }

    public static String getDefaultLanguage() {
        return "en";
    }

    public static String getAndroidId() {
        return Settings.Secure.getString(PrimeroApplication.getAppContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }
}
