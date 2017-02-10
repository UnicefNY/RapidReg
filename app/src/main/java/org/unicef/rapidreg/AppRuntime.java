package org.unicef.rapidreg;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import org.unicef.rapidreg.loadform.TemplateFormService;
import static android.content.Context.BIND_AUTO_CREATE;

public class AppRuntime {
    private Context applicationContext;

    private ServiceConnection templateCaseServiceConnection;
    private TemplateFormService.TemplateFormBinder templateFormBinder;

    public AppRuntime(Context context) {
        this.applicationContext = context;

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

    public void bindTemplateCaseService() {
        Intent intent = new Intent(applicationContext, TemplateFormService.class);
        applicationContext.bindService(intent, templateCaseServiceConnection, BIND_AUTO_CREATE);
    }

    public void unbindTemplateCaseService() {
        applicationContext.unbindService(templateCaseServiceConnection);
    }
}
