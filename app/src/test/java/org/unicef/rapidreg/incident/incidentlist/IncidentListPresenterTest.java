package org.unicef.rapidreg.incident.incidentlist;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.unicef.rapidreg.base.record.recordlist.spinner.SpinnerState;
import org.unicef.rapidreg.service.IncidentFormService;
import org.unicef.rapidreg.service.IncidentService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
public class IncidentListPresenterTest {

    @Mock
    IncidentService incidentService;

    @Mock
    IncidentFormService incidentFormService;

    @InjectMocks
    IncidentListPresenter incidentListPresenter;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void should_return_true_when_form_is_ready(){
        when(incidentFormService.isReady()).thenReturn(true);
        assertThat(incidentListPresenter.isFormReady(),is(true));
        verify(incidentFormService,times(1)).isReady();
    }

    @Test
    public void should_return_false_when_form_is_ready(){
        when(incidentFormService.isReady()).thenReturn(false);
        assertThat(incidentListPresenter.isFormReady(),is(false));
        verify(incidentFormService,times(1)).isReady();
    }

    @Test
    public void should_return_records_by_age_ASC_filter(){
        SpinnerState spinnerState = SpinnerState.AGE_ASC;

        List<Long> expected = new ArrayList<>();
        when(incidentService.getAllOrderByAgeASC()).thenReturn(expected);

        List<Long> actual = incidentListPresenter.getRecordsByFilter(spinnerState);

        assertThat("Should return records by age ASC filter when SpinnerState is AGE_ASC", actual, is(expected));
    }

    @Test
    public void should_return_records_by_age_DES_filter() throws Exception {
        SpinnerState spinnerState = SpinnerState.AGE_DES;

        List<Long> expected = new ArrayList<>();
        when(incidentService.getAllOrderByAgeDES()).thenReturn(expected);

        List<Long> actual = incidentListPresenter.getRecordsByFilter(spinnerState);

        assertThat("Should return records by age DES filter when SpinnerState is AGE_DES", actual, is(expected));
    }

    @Test
    public void should_return_records_by_registration_date_DES_filter() throws Exception {
        SpinnerState spinnerState = SpinnerState.INTERVIEW_DATE_DES;

        List<Long> expected = new ArrayList<>();
        when(incidentService.getAllOrderByDateDES()).thenReturn(expected);

        List<Long> actual = incidentListPresenter.getRecordsByFilter(spinnerState);

        assertThat("Should return records by registrarion date DES filter when SpinnerState is AGE_DES", actual, is(expected));
    }

    @Test
    public void should_return_records_by_registration_date_ASC_filter() throws Exception {
        SpinnerState spinnerState = SpinnerState.INQUIRY_DATE_ASC;

        List<Long> expected = new ArrayList<>();
        when(incidentService.getAllOrderByDateASC()).thenReturn(expected);

        List<Long> actual = incidentListPresenter.getRecordsByFilter(spinnerState);

        assertThat("Should return records by registrarion date ASC filter when SpinnerState is AGE_DES", actual, is(expected));
    }

    @Test
    public void should_return_displayed_index() throws Exception {
        when(incidentService.getAll()).thenReturn(Collections.EMPTY_LIST);

        int index = incidentListPresenter.calculateDisplayedIndex();

        assertThat("Should return 0 when incidents is empty", index, is(1));
    }

    @Test
    public void should_return_true_if_incident_form_is_ready() throws Exception {
        when(incidentFormService.isReady()).thenReturn(true);

        assertThat("Should return true if incident form is ready", incidentListPresenter.isFormReady(), is(true));
    }

}