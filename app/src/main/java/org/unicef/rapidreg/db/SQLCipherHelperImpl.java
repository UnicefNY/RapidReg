package org.unicef.rapidreg.db;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.BuildConfig;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.structure.database.DatabaseHelperListener;
import com.raizlabs.dbflow.android.sqlcipher.SQLCipherOpenHelper;

import org.mindrot.jbcrypt.BCrypt;
import org.unicef.rapidreg.PrimeroApplication;

public class SQLCipherHelperImpl extends SQLCipherOpenHelper {
    public static final String TAG = SQLCipherHelperImpl.class.getSimpleName();

    public SQLCipherHelperImpl(DatabaseDefinition definition, DatabaseHelperListener listener) {
        super(definition, listener);
    }

    @Override
    protected String getCipherSecret() {
        Log.d(TAG, String.valueOf(BuildConfig.DEBUG));
        return PrimeroApplication.isDebugMode() ? "primero" : generateCipherKey();
    }

    private String generateCipherKey() {
        Context ctx = PrimeroApplication.getAppContext();
        String androidId = Settings.Secure.getString(ctx.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();

        String buildInfo = Build.BOARD + Build.BRAND + Build.DEVICE + Build.DISPLAY
                + Build.FINGERPRINT + Build.HOST + Build.ID + Build.MANUFACTURER + Build.MODEL
                + Build.PRODUCT + Build.TAGS + Build.TYPE + Build.USER;

        return BCrypt.hashpw(buildInfo + deviceId + androidId, BCrypt.gensalt(12));
    }
}
