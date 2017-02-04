package org.unicef.rapidreg.service.impl;

import android.util.Log;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.forms.TracingTemplateForm;
import org.unicef.rapidreg.model.TracingForm;
import org.unicef.rapidreg.repository.TracingFormDao;
import org.unicef.rapidreg.service.TracingFormService;

public class TracingFormServiceImpl implements TracingFormService {
    public static final String TAG = TracingFormServiceImpl.class.getSimpleName();
    private TracingFormDao tracingFormDao;

    public TracingFormServiceImpl(TracingFormDao tracingFormDao) {
        this.tracingFormDao = tracingFormDao;
    }

    public boolean isReady() {
        return tracingFormDao.getTracingForm(PrimeroAppConfiguration.getApiBaseUrl()) != null && tracingFormDao
                .getTracingForm(PrimeroAppConfiguration.getApiBaseUrl()).getForm
                () != null;
    }

    public TracingTemplateForm getCPTemplate() {
        Blob form = tracingFormDao.getTracingForm(PrimeroAppConfiguration.getApiBaseUrl()).getForm();
        String formJson = new String(form.getBlob());
        if ("".equals(formJson)) {
            return null;
        }
        return new Gson().fromJson(formJson, TracingTemplateForm.class);
    }

    public void saveOrUpdate(TracingForm tracingForm) {
        TracingForm existingTracingForm = tracingFormDao.getTracingForm(PrimeroAppConfiguration.getApiBaseUrl());
        if (existingTracingForm == null) {
            Log.d(TAG, "save new tracing form");
            tracingForm.setUrl(PrimeroAppConfiguration.getApiBaseUrl());
            tracingForm.save();
        } else {
            Log.d(TAG, "update existing tracing form");
            existingTracingForm.setForm(tracingForm.getForm());
            existingTracingForm.update();
        }
    }
}
