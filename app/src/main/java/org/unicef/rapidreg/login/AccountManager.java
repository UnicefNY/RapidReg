package org.unicef.rapidreg.login;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.PrimeroApplication;

public class AccountManager {

    public static boolean isSignIn() {
        return PrimeroAppConfiguration.getCurrentUser() != null;
    }

    public static void doSignOut() {
        PrimeroAppConfiguration.setCurrentUser(null);
        PrimeroApplication.getAppRuntime().unbindTemplateCaseService();
        PrimeroApplication.getAppRuntime().unregisterAppRuntimeReceiver();
    }
}
