package org.unicef.rapidreg.loadform;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.event.LoadCPCaseFormEvent;
import org.unicef.rapidreg.event.LoadGBVCaseFormEvent;
import org.unicef.rapidreg.event.LoadGBVIncidentFormEvent;
import org.unicef.rapidreg.event.LoadTracingFormEvent;

import javax.inject.Inject;

public class TemplateFormService extends Service {
    private static final String TAG = TemplateFormService.class.getSimpleName();

    @Inject
    TemplateFormPresenter templateFormPresenter;

    private final TemplateFormBinder templateFormBinder = new TemplateFormBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return templateFormBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PrimeroApplication.get(PrimeroApplication.getAppContext())
                .getComponent()
                .inject(this);

        EventBus.getDefault().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "On start......");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "On destroy......");
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 1)
    public void onNeedLoadGBVCaseFormsEvent(LoadGBVCaseFormEvent event) {
        Log.d(TAG, "Loading GBV case form...");
        EventBus.getDefault().removeStickyEvent(event);
        templateFormPresenter.loadCaseForm(PrimeroAppConfiguration.MODULE_ID_GBV, new LoadCallback() {
            @Override
            public void onSuccess() {
                templateFormBinder.setCaseFormSyncFail(false);
            }

            @Override
            public void onFailure() {
                templateFormBinder.setCaseFormSyncFail(true);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 1)
    public void onNeedLoadCPCaseFormsEvent(LoadCPCaseFormEvent event) {
        Log.d(TAG, "Loading CP case form...");
        EventBus.getDefault().removeStickyEvent(event);
        templateFormPresenter.loadCaseForm(PrimeroAppConfiguration.MODULE_ID_CP, new LoadCallback() {
            @Override
            public void onSuccess() {
                templateFormBinder.setCaseFormSyncFail(false);
            }

            @Override
            public void onFailure() {
                templateFormBinder.setCaseFormSyncFail(true);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 1)
    public void onNeedLoadFormsEvent(final LoadTracingFormEvent event) {
        Log.d(TAG, "Loading Tracing request form...");
        EventBus.getDefault().removeStickyEvent(event);
        templateFormPresenter.loadTracingForm(new LoadCallback() {
            @Override
            public void onSuccess() {
                templateFormBinder.setTracingFormSyncFail(false);
            }

            @Override
            public void onFailure() {
                templateFormBinder.setTracingFormSyncFail(true);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 1)
    public void onNeedLoadGBVIncidentFormsEvent(final LoadGBVIncidentFormEvent event) {
        Log.d(TAG, "Loading GBV incident form...");
        EventBus.getDefault().removeStickyEvent(event);
        templateFormPresenter.loadIncidentForm(new LoadCallback() {
            @Override
            public void onSuccess() {
                templateFormBinder.setIncidentFormSyncFail(false);
            }

            @Override
            public void onFailure() {
                templateFormBinder.setIncidentFormSyncFail(true);
            }
        });
    }

    public class TemplateFormBinder extends Binder {

        private volatile boolean isCaseFormSyncFail;
        private volatile boolean isTracingRequestFormSyncFail;
        private volatile boolean isIncidentFormSyncFail;

        public boolean isCaseTemplateFormSyncFail() {
            return isCaseFormSyncFail;
        }

        public boolean isTracingTemplateFormSyncFail() {
            return isTracingRequestFormSyncFail;
        }

        public boolean isIncidentTemplateFormSyncFail() {
            return isIncidentFormSyncFail;
        }

        protected void setCaseFormSyncFail(boolean isSyncFail) {
            isCaseFormSyncFail = isSyncFail;
        }

        protected void setTracingFormSyncFail(boolean isSyncFail) {
            isTracingRequestFormSyncFail = isSyncFail;
        }

        protected void setIncidentFormSyncFail(boolean isSyncFail) {
            isIncidentFormSyncFail = isSyncFail;
        }
    }

    interface LoadCallback {
        void onSuccess();
        void onFailure();
    }
}
