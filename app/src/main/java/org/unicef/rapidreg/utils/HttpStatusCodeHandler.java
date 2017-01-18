package org.unicef.rapidreg.utils;

import android.util.Log;

import org.unicef.rapidreg.R;

public class HttpStatusCodeHandler {
    public static int getHttpStatusMessage(int statusCode) {
        Log.e("TAG", "getHttpStatusMessage: " + statusCode + "");
        switch (statusCode) {
            case 401:
                return R.string.login_failed_text;
            case 503:
                return R.string.login_service_unavailable_message;
            default:
                break;
        }
        return R.string.login_not_find_code_message;
    }
}
