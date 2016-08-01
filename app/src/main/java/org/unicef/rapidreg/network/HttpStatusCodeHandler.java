package org.unicef.rapidreg.network;

import android.util.Log;

public class HttpStatusCodeHandler {
    public static final String NOT_FIND_CODE_MESSAGE = "Not find status code!";
    public static final String UNAUTHORIZED_MESSAGE = "Sorry, incorrect username or password.";
    public static final String LOGIN_SUCCESS_MESSAGE = "Login successful";
    public static final String SERVICE_UNAVAILABLE_MESSAGE = "Service Unavailable!";

    public static String getHttpStatusMessage(int statusCode) {
        Log.e("TAG", "getHttpStatusMessage: " + statusCode + "");
        switch (statusCode) {
            case 401:
                return UNAUTHORIZED_MESSAGE;
            case 503:
                return SERVICE_UNAVAILABLE_MESSAGE;
            default:
                break;
        }
        return NOT_FIND_CODE_MESSAGE;
    }
}
