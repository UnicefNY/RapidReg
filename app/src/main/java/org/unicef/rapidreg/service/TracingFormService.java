package org.unicef.rapidreg.service;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.db.TracingFormDao;
import org.unicef.rapidreg.db.impl.TracingFormDaoImpl;
import org.unicef.rapidreg.forms.TracingFormRoot;
import org.unicef.rapidreg.model.TracingForm;

public class TracingFormService {
    public static final String TAG = TracingFormService.class.getSimpleName();
    private static TracingFormRoot tracingForm;
    private TracingFormDao tracingFormDao;

    public TracingFormService(TracingFormDao tracingFormDao) {
        this.tracingFormDao = tracingFormDao;
    }

    public boolean isFormReady() {
        Blob form = tracingFormDao.getTracingFormContent();

        return form != null;
    }

    public TracingFormRoot getCurrentForm() {
        if (tracingForm == null) {
            updateCachedForm();
        }
        return tracingForm;
    }

    public void saveOrUpdateForm(TracingForm tracingForm) {
        TracingForm existingTracingForm = tracingFormDao.getTracingForm();

        if (existingTracingForm == null) {
            Log.d(TAG, "save new tracing form");
            tracingForm.save();
        } else {
            Log.d(TAG, "update existing tracing form");
            existingTracingForm.setForm(tracingForm.getForm());
            existingTracingForm.update();
        }

        updateCachedForm();
    }

    private void updateCachedForm() {
        Blob form = tracingFormDao.getTracingFormContent();

        String formJson = new String(form.getBlob());
        tracingForm = TextUtils.isEmpty(formJson) ?
                null : new Gson().fromJson(formJson, TracingFormRoot.class);
    }
}
