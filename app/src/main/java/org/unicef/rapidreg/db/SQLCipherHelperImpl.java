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
        String android_id = Settings.Secure.getString(ctx.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        String device_id = tm.getDeviceId();

        String build_info = Build.BOARD + Build.BRAND + Build.DEVICE + Build.DISPLAY
                + Build.FINGERPRINT + Build.HOST + Build.ID + Build.MANUFACTURER + Build.MODEL
                + Build.PRODUCT + Build.TAGS + Build.TYPE + Build.USER;

        return BCrypt.hashpw(build_info + device_id + android_id, BCrypt.gensalt(12));
    }
}
