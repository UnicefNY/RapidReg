package org.unicef.rapidreg.childcase.caselist;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.unicef.rapidreg.base.record.recordlist.spinner.SpinnerState;
import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.CasePhotoService;
import org.unicef.rapidreg.service.CaseService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
public class CaseListPresenterTest {

    @Mock
    CaseService caseService;

    @Mock
    CaseFormService caseFormService;

    @Mock
    CasePhotoService casePhotoService;

    @InjectMocks
    CaseListPresenter caseListPresenter;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void should_return_records_by_age_ASC_filter() throws Exception {
        SpinnerState spinnerState = SpinnerState.AGE_ASC;

        List<Long> expected = new ArrayList<>();
        when(caseService.getAllOrderByAgeASC()).thenReturn(expected);

        List<Long> actual = caseListPresenter.getRecordsByFilter(spinnerState);

        assertThat("Should return records by age ASC filter when SpinnerState is AGE_ASC", actual, is(expected));
    }

    @Test
    public void should_return_records_by_age_DES_filter() throws Exception {
        SpinnerState spinnerState = SpinnerState.AGE_DES;

        List<Long> expected = new ArrayList<>();
        when(caseService.getAllOrderByAgeDES()).thenReturn(expected);

        List<Long> actual = caseListPresenter.getRecordsByFilter(spinnerState);

        assertThat("Should return records by age DES filter when SpinnerState is AGE_DES", actual, is(expected));
    }

    @Test
    public void should_return_records_by_registration_date_DES_filter() throws Exception {
        SpinnerState spinnerState = SpinnerState.REG_DATE_DES;

        List<Long> expected = new ArrayList<>();
        when(caseService.getAllOrderByDateDES()).thenReturn(expected);

        List<Long> actual = caseListPresenter.getRecordsByFilter(spinnerState);

        assertThat("Should return records by registrarion date DES filter when SpinnerState is AGE_DES", actual, is(expected));
    }

    @Test
    public void should_return_records_by_registration_date_ASC_filter() throws Exception {
        SpinnerState spinnerState = SpinnerState.REG_DATE_ASC;

        List<Long> expected = new ArrayList<>();
        when(caseService.getAllOrderByDateASC()).thenReturn(expected);

        List<Long> actual = caseListPresenter.getRecordsByFilter(spinnerState);

        assertThat("Should return records by registrarion date ASC filter when SpinnerState is AGE_DES", actual, is(expected));
    }

    @Test
    public void should_return_displayed_index_when_case_is_empty() throws Exception {
        when(caseService.getAll()).thenReturn(Collections.EMPTY_LIST);

        int index = caseListPresenter.calculateDisplayedIndex();

        assertThat("Should return 1 when cases is empty", index, is(1));
    }

    @Test
    public void should_return_zero_index_when_case_exits() throws Exception {
        List<Case> cases = Arrays.asList(new Case());
        when(caseService.getAll()).thenReturn(cases);

        assertThat("Should return 0 when case exits", caseListPresenter.calculateDisplayedIndex(), is(0));
    }

    @Test
    public void should_return_true_if_case_form_is_ready() throws Exception {
        when(caseFormService.isReady()).thenReturn(true);

        assertThat("Should return true if case form is ready", caseListPresenter.isFormReady(), is(true));
    }
}