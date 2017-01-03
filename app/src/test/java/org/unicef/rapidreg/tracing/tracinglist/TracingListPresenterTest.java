package org.unicef.rapidreg.tracing.tracinglist;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.unicef.rapidreg.base.record.recordlist.spinner.SpinnerState;
import org.unicef.rapidreg.service.TracingFormService;
import org.unicef.rapidreg.service.TracingService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
public class TracingListPresenterTest {
    @Mock
    TracingService tracingService;

    @Mock
    TracingFormService tracingFormService;

    @InjectMocks
    TracingListPresenter tracingListPresenter;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void should_return_true_when_form_is_ready() {
        when(tracingFormService.isReady()).thenReturn(true);
        assertThat(tracingListPresenter.isFormReady(), is(true));
        verify(tracingFormService, times(1)).isReady();
    }

    @Test
    public void should_return_false_when_form_is_ready() {
        when(tracingFormService.isReady()).thenReturn(false);
        assertThat(tracingListPresenter.isFormReady(), is(false));
        verify(tracingFormService, times(1)).isReady();
    }

    @Test
    public void should_return_records_by_registration_date_DES_filter() throws Exception {
        SpinnerState spinnerState = SpinnerState.INQUIRY_DATE_DES;

        List<Long> expected = new ArrayList<>();
        when(tracingService.getAllOrderByDateDES()).thenReturn(expected);

        List<Long> actual = tracingListPresenter.getRecordsByFilter(spinnerState);

        assertThat("Should return records by registrarion date DES filter when SpinnerState is " +
                "AGE_DES", actual, is(expected));
    }

    @Test
    public void should_return_records_by_registration_date_ASC_filter() throws Exception {
        SpinnerState spinnerState = SpinnerState.INQUIRY_DATE_ASC;

        List<Long> expected = new ArrayList<>();
        when(tracingService.getAllOrderByDateASC()).thenReturn(expected);

        List<Long> actual = tracingListPresenter.getRecordsByFilter(spinnerState);

        assertThat("Should return records by registrarion date ASC filter when SpinnerState is " +
                "AGE_DES", actual, is(expected));
    }

    @Test
    public void should_return_displayed_index() throws Exception {
        when(tracingService.getAll()).thenReturn(Collections.EMPTY_LIST);

        int index = tracingListPresenter.calculateDisplayedIndex();

        assertThat("Should return 0 when tracing_request is empty", index, is(1));
    }

    @Test
    public void should_return_true_if_tracing_form_is_ready() throws Exception {
        when(tracingFormService.isReady()).thenReturn(true);

        assertThat("Should return true if tracing form is ready", tracingListPresenter
                .isFormReady(), is(true));
    }
}