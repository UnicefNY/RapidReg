package org.unicef.rapidreg.incident;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.unicef.rapidreg.forms.IncidentTemplateForm;
import org.unicef.rapidreg.forms.Section;
import org.unicef.rapidreg.model.IncidentForm;
import org.unicef.rapidreg.service.FormRemoteService;
import org.unicef.rapidreg.service.IncidentFormService;

import edu.emory.mathcs.backport.java.util.Arrays;
import rx.Observable;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
public class IncidentPresenterTest {

    @Mock
    IncidentFormService incidentFormService;

    @Mock
    FormRemoteService authService;

    @InjectMocks
    IncidentPresenter incidentPresenter;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void should_save_form_when_give_record_form_and_module_id() throws Exception {
        IncidentTemplateForm incidentTemplateForm = createIncidentTemplateForm();
        String moduleId = "primeromodule-gbv";
        incidentPresenter.saveForm(incidentTemplateForm, moduleId);
        verify(incidentFormService, times(1)).saveOrUpdate(any(IncidentForm.class));
    }

    @Test
    public void should_load_incident_form_when_give_cookie_and_module_id() throws Exception {
        IncidentTemplateForm incidentTemplateForm = createIncidentTemplateForm();
        String moduleId = "primeromodule-gbv";
        Observable<IncidentTemplateForm> observable = Observable.just(incidentTemplateForm);
        when(authService.getIncidentForm("cookie", "en", true, "incident",
                "primeromodule-gbv")).thenReturn(observable);
        incidentPresenter.loadIncidentForm("cookie", moduleId);
        verify(incidentFormService, times(1)).saveOrUpdate(any(IncidentForm.class));
        assertFalse("Should mark sync successful.", incidentPresenter.isFormSyncFail());
    }

    @Test
    public void should_show_error_when_sync_form_fail() throws Exception {
        Observable observable = Observable.error(new Exception());
        when(authService.getIncidentForm("cookie", "en", true, "incident", "primeromodule-gbv"))
                .thenReturn(observable);

        incidentPresenter.loadIncidentForm("cookie", "primeromodule-gbv");

        assertTrue("Should mark sync fail.", incidentPresenter.isFormSyncFail());
    }

    private IncidentTemplateForm createIncidentTemplateForm() {
        IncidentTemplateForm incidentTemplateForm = new IncidentTemplateForm();
        Section section = createSection();
        incidentTemplateForm.setSections(Arrays.asList(new Section[]{section}));
        return incidentTemplateForm;
    }

    private Section createSection() {
        Section section = new Section();
        section.setBaseLanguage("base_language");
        section.setOrder(12);
        return section;
    }

}