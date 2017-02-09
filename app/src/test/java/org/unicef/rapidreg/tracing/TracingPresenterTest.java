package org.unicef.rapidreg.tracing;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.forms.Section;
import org.unicef.rapidreg.forms.TracingTemplateForm;
import org.unicef.rapidreg.model.TracingForm;
import org.unicef.rapidreg.service.FormRemoteService;
import org.unicef.rapidreg.service.TracingFormService;

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
@PrepareForTest({PrimeroAppConfiguration.class})
public class TracingPresenterTest {
    @Mock
    TracingFormService tracingFormService;

    @Mock
    FormRemoteService authService;

    @InjectMocks
    TracingPresenter tracingPresenter;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        PowerMockito.mockStatic(PrimeroAppConfiguration.class);
    }

    @Test
    public void should_save_form_when_give_record_form_and_module_id() throws Exception {
        TracingTemplateForm tracingTemplateForm = createTracingTemplateForm();
        String moduleId = "primeromodule-cp";
        tracingPresenter.saveForm(tracingTemplateForm, moduleId);

        verify(tracingFormService, times(1)).saveOrUpdate(any(TracingForm.class));
    }

//    @Test
//    public void should_load_tracing_form_when_give_cookie_and_module_id() throws Exception {
//        TracingTemplateForm tracingTemplateForm = createTracingTemplateForm();
//        Observable<TracingTemplateForm> observable = Observable.just(tracingTemplateForm);
//        when(authService.getTracingForm("cookie", "en", true, "tracing_request", "primeromodule-cp"))
//                .thenReturn(observable);
//        Mockito.when(PrimeroAppConfiguration.getCookie()).thenReturn("cookie");
//        Mockito.when(PrimeroAppConfiguration.getDefaultLanguage()).thenReturn("en");
//
//        tracingPresenter.loadTracingForm();
//
//        verify(tracingFormService, times(1)).saveOrUpdate(any(TracingForm.class));
//        assertFalse("Should mark sync successful.", tracingPresenter.isFormSyncFail());
//    }

//    @Test
//    public void should_show_error_when_sync_form_fail() throws Exception {
//        Observable observable = Observable.error(new Exception());
//        when(authService.getTracingForm("cookie", "en", true, "tracing_request", "primeromodule-cp"))
//                .thenReturn(observable);
//
//        Mockito.when(PrimeroAppConfiguration.getCookie()).thenReturn("cookie");
//        Mockito.when(PrimeroAppConfiguration.getDefaultLanguage()).thenReturn("en");
//
//        tracingPresenter.loadTracingForm();
//
//        assertTrue("Should mark sync fail.", tracingPresenter.isFormSyncFail());
//    }

    private TracingTemplateForm createTracingTemplateForm() {
        TracingTemplateForm tracingTemplateForm = new TracingTemplateForm();
        Section section = createSection();
        tracingTemplateForm.setSections(Arrays.asList(new Section[]{section}));
        return tracingTemplateForm;
    }

    private Section createSection() {
        Section section = new Section();
        section.setBaseLanguage("base_language");
        section.setOrder(12);
        return section;
    }
}