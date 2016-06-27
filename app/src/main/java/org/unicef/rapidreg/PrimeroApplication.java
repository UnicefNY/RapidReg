package org.unicef.rapidreg;

import android.app.Application;
import android.content.Context;

import com.raizlabs.android.dbflow.config.DatabaseConfig;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.DatabaseHelperListener;
import com.raizlabs.android.dbflow.structure.database.OpenHelper;

import org.unicef.rapidreg.db.PrimeroDB;
import org.unicef.rapidreg.db.SQLCipherHelperImpl;
import org.unicef.rapidreg.service.cache.CasePhotoCache;

public class PrimeroApplication extends Application {
    private static Context context;

    public static Context getAppContext() {
        return context;
    }

    public static boolean isDebugMode() {
        String pkgName = context.getPackageName();
        return (pkgName != null && pkgName.endsWith(".debug"));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        CasePhotoCache.initApplicationPackageName(context.getPackageName());
        initDB();
    }

    // TODO: need to realise get in progress Sychronization tasks
    public Object getSyncTask() {
        return null;
    }

    private void initDB() {
        FlowManager.init(new FlowConfig.Builder(this)
                .addDatabaseConfig(new DatabaseConfig.Builder(PrimeroDB.class)
                        .openHelper(new DatabaseConfig.OpenHelperCreator() {
                            @Override
                            public OpenHelper createHelper(DatabaseDefinition databaseDefinition,
                                                           DatabaseHelperListener helperListener) {
                                return new SQLCipherHelperImpl(databaseDefinition, helperListener);
                            }
                        }).build())
                .build());
    }
}
