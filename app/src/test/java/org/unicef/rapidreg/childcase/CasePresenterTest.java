package org.unicef.rapidreg.childcase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.unicef.rapidreg.forms.CaseTemplateForm;
import org.unicef.rapidreg.forms.Section;
import org.unicef.rapidreg.model.CaseForm;
import org.unicef.rapidreg.service.AuthService;
import org.unicef.rapidreg.service.CaseFormService;

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
public class CasePresenterTest {

    @Mock
    CaseFormService caseFormService;

    @Mock
    AuthService authService;

    @InjectMocks
    CasePresenter casePresenter;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void should_save_form_when_give_record_form_and_module_id() throws Exception {
        CaseTemplateForm caseTemplateForm = createCaseTemplateForm();
        String moduleId = "primeromodule-cp";
        casePresenter.saveForm(caseTemplateForm, moduleId);

        verify(caseFormService, times(1)).saveOrUpdate(any(CaseForm.class));
    }

    @Test
    public void should_load_case_form_when_give_cookie_and_module_id() throws Exception {
        CaseTemplateForm caseTemplateForm = createCaseTemplateForm();
        Observable<CaseTemplateForm> observable = Observable.just(caseTemplateForm);
        when(authService.getCaseForm("cookie", "English", true, "case", "primeromodule-cp"))
                .thenReturn(observable);

        casePresenter.loadCaseForm("English", "cookie", "primeromodule-cp");

        verify(caseFormService, times(1)).saveOrUpdate(any(CaseForm.class));
        assertFalse("Should mark sync successful.", casePresenter.isFormSyncFail());
    }

    @Test
    public void should_show_error_when_sync_form_fail() throws Exception {
        Observable observable = Observable.error(new Exception());
        when(authService.getCaseForm("cookie", "English", true, "case", "primeromodule-cp"))
                .thenReturn(observable);

        casePresenter.loadCaseForm("English", "cookie", "primeromodule-cp");

        assertTrue("Should mark sync fail.", casePresenter.isFormSyncFail());
    }

    private CaseTemplateForm createCaseTemplateForm() {
        CaseTemplateForm caseTemplateForm = new CaseTemplateForm();
        Section section = createSection();
        caseTemplateForm.setSections(Arrays.asList(new Section[]{section}));
        return caseTemplateForm;
    }

    private Section createSection() {
        Section section = new Section();
        section.setBaseLanguage("base_language");
        section.setOrder(12);
        return section;
    }
}