package org.unicef.rapidreg.incident;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.unicef.rapidreg.service.IncidentFormService;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class IncidentPresenterTest {

    @Mock
    IncidentFormService incidentFormService;

    @InjectMocks
    IncidentPresenter incidentPresenter;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void should_return_false_when_incident_form_not_ready() throws Exception {
        when(incidentFormService.isReady()).thenReturn(false);

        assertThat("Should return false", incidentPresenter.isFormReady(), is(false));
        verify(incidentFormService, times(1)).isReady();
    }

    @Test
    public void should_return_true_when_incident_form_is_ready() throws Exception {
        when(incidentFormService.isReady()).thenReturn(true);

        assertThat("Should return true", incidentPresenter.isFormReady(), is(true));
        verify(incidentFormService, times(1)).isReady();
    }
}