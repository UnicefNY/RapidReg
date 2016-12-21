package org.unicef.rapidreg.incident.incidentregister;

import android.os.Bundle;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.RobolectricTestRunner;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterView;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.forms.IncidentTemplateForm;
import org.unicef.rapidreg.forms.Section;
import org.unicef.rapidreg.model.Incident;
import org.unicef.rapidreg.service.IncidentFormService;
import org.unicef.rapidreg.service.IncidentService;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.cache.ItemValuesMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.emory.mathcs.backport.java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(RobolectricTestRunner.class)
@PrepareForTest({RecordService.class})
public class IncidentRegisterPresenterTest {

    @Mock
    IncidentService incidentService;

    @Mock
    IncidentFormService incidentFormService;

    @InjectMocks
    private IncidentRegisterPresenter incidentRegisterPresenter;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void should_return_true_record_id_when_get_record_id() {
        Bundle bundle = new Bundle();
        bundle.putLong(IncidentService.INCIDENT_PRIMARY_ID, 2);
        assertThat(incidentRegisterPresenter.getRecordId(bundle), is(2L));
    }

    @Test
    public void should_return_default_record_id_when_get_record_id() {
        Bundle bundle = new Bundle();
        assertThat(incidentRegisterPresenter.getRecordId(bundle), is(-100L));
    }

    @Test
    public void required_field_is_true_when_save_record() throws IOException {
        ItemValuesMap itemValuesMap = new ItemValuesMap();
        itemValuesMap.addBooleanItem("is_case", true);
        itemValuesMap.addNumberItem("age", 12);
        RecordRegisterView.SaveRecordCallback callback = mock(RecordRegisterView
                .SaveRecordCallback.class);

        IncidentTemplateForm incidentTemplateForm = mock(IncidentTemplateForm.class);

        when(incidentFormService.getGBVTemplate()).thenReturn(incidentTemplateForm);
        when(incidentService.validateRequiredFields(incidentTemplateForm, itemValuesMap))
                .thenReturn(true);
        Incident record = mock(Incident.class);
        when(incidentService.saveOrUpdate(itemValuesMap)).thenReturn(record);

        when(record.getRegistrationDate()).thenReturn(new Date());
        when(record.getUniqueId()).thenReturn("");
        when(record.getId()).thenReturn(1L);

        incidentRegisterPresenter.saveRecord(itemValuesMap,new ArrayList<String>(),callback);

        verify(incidentService, times(1)).saveOrUpdate(itemValuesMap);
        verify(callback,times(1)).onSaveSuccessful(anyInt());
    }

    @Test
    public void required_Field_is_false_when_save_record() {
        ItemValuesMap itemValuesMap = new ItemValuesMap();
        itemValuesMap.addBooleanItem("is_case", true);
        itemValuesMap.addNumberItem("age", 12);
        RecordRegisterView.SaveRecordCallback callback = mock(RecordRegisterView
                .SaveRecordCallback.class);

        IncidentTemplateForm incidentTemplateForm = mock(IncidentTemplateForm.class);

        when(incidentFormService.getGBVTemplate()).thenReturn(incidentTemplateForm);
        when(incidentService.validateRequiredFields(incidentTemplateForm, itemValuesMap))
                .thenReturn(false);
    }

    @Test
    public void should_return_get_fields_from_incident_form_sections() {
        Field miniFormField = mock(Field.class);
        Field otherFormField = mock(Field.class);
        Section section = mock(Section.class);
        IncidentTemplateForm incidentTemplateForm = mock(IncidentTemplateForm.class);

        List<Field> fields = Arrays.asList(new Field[]{miniFormField, otherFormField});
        List<Section> sections = Arrays.asList(new Section[]{section});

        when(miniFormField.isShowOnMiniForm()).thenReturn(true);
        when(miniFormField.isPhotoUploadBox()).thenReturn(false);
        when(otherFormField.isShowOnMiniForm()).thenReturn(false);

        when(section.getFields()).thenReturn(fields);
        when(incidentTemplateForm.getSections()).thenReturn(sections);
        when(incidentFormService.getGBVTemplate()).thenReturn(incidentTemplateForm);

        List<Field> actual = incidentRegisterPresenter.getFields();

        assertThat("Should contain mini form field", actual.contains(miniFormField), is(true));
        assertThat("Should not contain full form field", actual.contains(otherFormField), is
                (false));
        verify(incidentFormService, times(1)).getGBVTemplate();

    }

}