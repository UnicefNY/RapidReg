package org.unicef.rapidreg.service.impl;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.base.RecordConfiguration;
import org.unicef.rapidreg.db.IncidentFormDao;
import org.unicef.rapidreg.forms.IncidentTemplateForm;
import org.unicef.rapidreg.model.IncidentForm;
import org.unicef.rapidreg.service.IncidentFormService;

public class IncidentFormServiceImpl implements IncidentFormService {
    public static final String TAG = IncidentFormService.class.getSimpleName();
    private IncidentFormDao incidentFormDao;

    public IncidentFormServiceImpl(IncidentFormDao incidentFormDao) {
        this.incidentFormDao = incidentFormDao;
    }

    public boolean isReady() {
        IncidentForm incidentForm = incidentFormDao.getIncidentForm(RecordConfiguration
                .MODULE_ID_GBV);
        return incidentForm != null && incidentForm.getForm() != null;
    }

    @Override
    public IncidentTemplateForm getGBVTemplate() {
        Blob form = incidentFormDao.getIncidentForm(RecordConfiguration.MODULE_ID_GBV).getForm();
        return getIncidentTemplateForm(form);
    }

    private IncidentTemplateForm getIncidentTemplateForm(Blob form) {
        String formJson = new String(form.getBlob());
        if ("".equals(formJson)) {
            return null;
        }
        return new Gson().fromJson(formJson, IncidentTemplateForm.class);
    }

    public void saveOrUpdate(IncidentForm incidentForm) {
        IncidentForm existingIncidentForm = incidentFormDao.getIncidentForm(incidentForm.
                getModuleId());
        if (existingIncidentForm == null) {
            incidentForm.save();
        } else {
            existingIncidentForm.setForm(incidentForm.getForm());
            existingIncidentForm.update();
        }
    }
}
