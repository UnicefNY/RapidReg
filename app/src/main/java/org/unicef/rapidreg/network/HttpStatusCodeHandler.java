package org.unicef.rapidreg.network;

public class HttpStatusCodeHandler {

    public static final String NOT_FIND_CODE_MESSAGE = "Not find status code!";
    public static final String UNAUTHORIZED_MESSAGE = "Unauthorized!";

    public static String getHttpStatusMessage(int statusCode) {
        switch (statusCode) {
            case 401:
                return UNAUTHORIZED_MESSAGE;
            default:
                break;
        }
        return NOT_FIND_CODE_MESSAGE;
    }
}
