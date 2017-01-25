package org.unicef.rapidreg.service.impl;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.data.Blob;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.base.RecordConfiguration;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.forms.Section;
import org.unicef.rapidreg.repository.IncidentFormDao;
import org.unicef.rapidreg.forms.IncidentTemplateForm;
import org.unicef.rapidreg.model.IncidentForm;
import org.unicef.rapidreg.service.IncidentFormService;

import java.util.List;

public class IncidentFormServiceImpl implements IncidentFormService {
    public static final String TAG = IncidentFormService.class.getSimpleName();
    private static final String ENG_VALUE = "en";
    public static final String SECTION_TYPE_OF_VIOLENCE = "Type of Violence";
    public static final String SECTION_GBV_INCIDENT = "GBV Incident";
    private static final String FIELD_INCIDENT_TYPE_OF_VIOLENCE = "type_of_incident_violence";
    private static final String FIELD_LOCATION_INCIDENT = "location";
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

    @Override
    public List<String> getViolenceTypeList() {
        IncidentTemplateForm incidentTemplateForm = getGBVTemplate();
        List<Section> sections = incidentTemplateForm.getSections();
        Section violenceSection = null;
        for (Section section : sections) {
            if (SECTION_TYPE_OF_VIOLENCE.equals(section.getName().get(PrimeroAppConfiguration.getDefaultLanguage()))) {
                violenceSection = section;
                break;
            }
        }
        List<Field> fields = violenceSection.getFields();
        Field violenceTypeField = null;
        for (Field field : fields) {
            if (FIELD_INCIDENT_TYPE_OF_VIOLENCE.equals(field.getName())) {
                violenceTypeField = field;
                break;
            }
        }
        return violenceTypeField.getSelectOptions();
    }

    @Override
    public List<String> getLocationList() {
        IncidentTemplateForm incidentTemplateForm = getGBVTemplate();
        List<Section> sections = incidentTemplateForm.getSections();
        Section locationSection = null;
        for (Section section : sections) {
            if (SECTION_GBV_INCIDENT.equals(section.getName().get(PrimeroAppConfiguration.getDefaultLanguage()))) {
                locationSection = section;
                break;
            }
        }
        Field locationField = null;
        for (Field field : locationSection.getFields()) {
            if (FIELD_LOCATION_INCIDENT.equals(field.getName())) {
                locationField = field;
                break;
            }
        }
        return locationField.getSelectOptions();
    }
}
