package org.unicef.rapidreg.loadform;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.event.LoadCPCaseFormEvent;
import org.unicef.rapidreg.event.LoadGBVCaseFormEvent;

import javax.inject.Inject;

public class TemplateFormService extends Service {

    @Inject
    FormPresenter formPresenter;

    private static final String TAG = TemplateFormService.class.getSimpleName();

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
        super.onCreate();
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
        EventBus.getDefault().removeStickyEvent(event);
        casePresenter.loadCaseForm(PrimeroAppConfiguration.MODULE_ID_GBV);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 1)
    public void onNeedLoadCPCaseFormsEvent(LoadCPCaseFormEvent event) {
        EventBus.getDefault().removeStickyEvent(event);
        casePresenter.loadCaseForm(PrimeroAppConfiguration.MODULE_ID_CP);
    }
}
