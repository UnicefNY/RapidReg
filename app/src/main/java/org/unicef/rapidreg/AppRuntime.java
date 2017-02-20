package org.unicef.rapidreg;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import org.unicef.rapidreg.broadcast.AppRuntimeReceiver;
import org.unicef.rapidreg.loadform.TemplateFormService;
import org.unicef.rapidreg.repository.sharedpref.PrimeroDataPref;
import org.unicef.rapidreg.sync.SyncStatisticData;

import static android.content.Context.BIND_AUTO_CREATE;

public class AppRuntime {
    private static final String TAG = AppRuntime.class.getSimpleName();

    private Context applicationContext;

    private ServiceConnection templateCaseServiceConnection;
    private TemplateFormService.TemplateFormBinder templateFormBinder;
    private AppRuntimeReceiver appRuntimeReceiver;

    private PrimeroDataPref dataPref;

    public AppRuntime(Context context) {
        this.applicationContext = context;
        this.dataPref = new PrimeroDataPref(context);

        initTemplateCaseServiceConnection();
    }

    private void initTemplateCaseServiceConnection() {
        templateCaseServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                templateFormBinder = (TemplateFormService.TemplateFormBinder) service;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                templateFormBinder = null;
            }
        };
    }

    public boolean isCaseFormSyncFail() {
        if (templateFormBinder == null) {
            return false;
        }
        return templateFormBinder.isCaseTemplateFormSyncFail();
    }

    public boolean isTracingFormSyncFail() {
        if (templateFormBinder == null) {
            return false;
        }
        return templateFormBinder.isTracingTemplateFormSyncFail();
    }

    public boolean isIncidentFormSyncFail() {
        if (templateFormBinder == null) {
            return false;
        }
        return templateFormBinder.isIncidentTemplateFormSyncFail();
    }

    public void storeSyncData(SyncStatisticData syncData) {
        dataPref.storeSyncData(syncData);
    }

    public SyncStatisticData loadSyncData() {
        return dataPref.loadSyncData();
    }

    public void bindTemplateCaseService() {
        Log.d(TAG, "TemplateCaseService binded...");
        Intent intent = new Intent(applicationContext, TemplateFormService.class);
        applicationContext.bindService(intent, templateCaseServiceConnection, BIND_AUTO_CREATE);
    }

    public void unbindTemplateCaseService() {
        Log.d(TAG, "TemplateCaseService unbinded...");
        if (templateFormBinder != null) {
            applicationContext.unbindService(templateCaseServiceConnection);
            templateFormBinder = null;
        }
    }

    public void registerAppRuntimeReceiver() {
        appRuntimeReceiver = new AppRuntimeReceiver();
        final IntentFilter homeFilter = new IntentFilter();
        homeFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        homeFilter.addAction(Intent.ACTION_SCREEN_OFF);
        applicationContext.registerReceiver(appRuntimeReceiver, homeFilter);
        Log.d(TAG, "AppRuntimeReceiver registered...");
    }

    public void unregisterAppRuntimeReceiver() {
        if (null != appRuntimeReceiver) {
            try {
                applicationContext.unregisterReceiver(appRuntimeReceiver);
                appRuntimeReceiver = null;
                Log.d(TAG, "AppRuntimeReceiver unregistered...");
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
        }
    }
}
