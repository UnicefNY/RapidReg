package org.unicef.rapidreg.tracing.tracingregister;

import android.os.Bundle;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.RobolectricTestRunner;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.TracingFormService;
import org.unicef.rapidreg.service.TracingService;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.unicef.rapidreg.service.TracingService.TRACING_PRIMARY_ID;

@RunWith(RobolectricTestRunner.class)
@PrepareForTest({RecordService.class})
public class TracingRegisterPresenterTest {
    @Mock
    TracingService tracingService;

    @Mock
    TracingFormService tracingFormService;

    @InjectMocks
    private TracingRegisterPresenter tracingRegisterPresenter;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void should_return_true_record_id_when_get_record_id() {
        Bundle bundle = new Bundle();
        bundle.putLong(TRACING_PRIMARY_ID, 2);
        assertThat(tracingRegisterPresenter.getRecordId(bundle), is(2L));
    }
//
//    @Test
//    public void should_return_default_record_id_when_get_record_id() {
//        Bundle bundle = new Bundle();
//        assertThat(incidentRegisterPresenter.getRecordId(bundle), is(-100L));
//    }
//
//    @Test
//    public void required_field_is_true_when_save_record() throws IOException {
//        ItemValuesMap itemValuesMap = new ItemValuesMap();
//        itemValuesMap.addBooleanItem("is_case", true);
//        itemValuesMap.addNumberItem("age", 12);
//        RecordRegisterView.SaveRecordCallback callback = mock(RecordRegisterView
//                .SaveRecordCallback.class);
//
//        IncidentTemplateForm incidentTemplateForm = mock(IncidentTemplateForm.class);
//
//        when(incidentFormService.getGBVTemplate()).thenReturn(incidentTemplateForm);
//        when(incidentService.validateRequiredFields(incidentTemplateForm, itemValuesMap))
//                .thenReturn(true);
//        Incident record = mock(Incident.class);
//        when(incidentService.saveOrUpdate(itemValuesMap)).thenReturn(record);
//
//        when(record.getRegistrationDate()).thenReturn(new Date());
//        when(record.getUniqueId()).thenReturn("");
//        when(record.getId()).thenReturn(1L);
//
//        incidentRegisterPresenter.saveRecord(itemValuesMap, new ArrayList<String>(), callback);
//
//        verify(incidentService, times(1)).saveOrUpdate(itemValuesMap);
//        verify(callback, times(1)).onSaveSuccessful(anyInt());
//    }
//
//    @Test
//    public void required_Field_is_false_when_save_record() {
//        ItemValuesMap itemValuesMap = new ItemValuesMap();
//        itemValuesMap.addBooleanItem("is_case", true);
//        itemValuesMap.addNumberItem("age", 12);
//        RecordRegisterView.SaveRecordCallback callback = mock(RecordRegisterView
//                .SaveRecordCallback.class);
//
//        IncidentTemplateForm incidentTemplateForm = mock(IncidentTemplateForm.class);
//
//        when(incidentFormService.getGBVTemplate()).thenReturn(incidentTemplateForm);
//        when(incidentService.validateRequiredFields(incidentTemplateForm, itemValuesMap))
//                .thenReturn(false);
//
//        incidentRegisterPresenter.saveRecord(itemValuesMap, new ArrayList<String>(), callback);
//        verify(callback, times(1)).onRequiredFieldNotFilled();
//    }
//
//    @Test
//    public void should_return_item_values_map_by_record_id() throws JSONException,
// ParseException {
//        Long recordId = 1L;
//        Incident incidentItem = new Incident();
//        String uniqueId = "89fdj89r34jif9af90a";
//        incidentItem.setContent(new Blob("{\"age\":\"12\"}".getBytes()));
//
//        incidentItem.setUniqueId(uniqueId);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd ");
//        Date date = sdf.parse("2012-12-06 ");
//
//        incidentItem.setRegistrationDate(date);
//        when(incidentService.getById(recordId)).thenReturn(incidentItem);
//
//        ItemValuesMap itemValuesMap = incidentRegisterPresenter.getItemValuesByRecordId(recordId);
//
//        assertThat(itemValuesMap.getAsString("incident_id"), is("89fdj89r34jif9af90a"));
//    }
//
//    @Test
//    public void should_return_get_fields_from_incident_form_sections() {
//        Field miniFormField = mock(Field.class);
//        Field otherFormField = mock(Field.class);
//        Section section = mock(Section.class);
//        IncidentTemplateForm incidentTemplateForm = mock(IncidentTemplateForm.class);
//
//        List<Field> fields = Arrays.asList(new Field[]{miniFormField, otherFormField});
//        List<Section> sections = Arrays.asList(new Section[]{section});
//
//        when(miniFormField.isShowOnMiniForm()).thenReturn(true);
//        when(miniFormField.isPhotoUploadBox()).thenReturn(false);
//        when(otherFormField.isShowOnMiniForm()).thenReturn(false);
//
//        when(section.getFields()).thenReturn(fields);
//        when(incidentTemplateForm.getSections()).thenReturn(sections);
//        when(incidentFormService.getGBVTemplate()).thenReturn(incidentTemplateForm);
//
//        List<Field> actual = incidentRegisterPresenter.getFields();
//
//        assertThat("Should contain mini form field", actual.contains(miniFormField), is(true));
//        assertThat("Should not contain full form field", actual.contains(otherFormField), is
//                (false));
//        verify(incidentFormService, times(1)).getGBVTemplate();
//
//    }
//
//    @Test
//    public void should_return_get_fields_when_position_is_known_and_form_is_not_null(){
//        IncidentTemplateForm form = new IncidentTemplateForm();
//        when(incidentFormService.getGBVTemplate()).thenReturn(form);
//        Section section = new Section();
//        Field miniFormField = new Field();
//        Field otherFormField = new Field();
//        List<Field> fields = Arrays.asList(new Field[]{miniFormField, otherFormField});
//        List<Section> sections = Arrays.asList(new Section[]{section});
//        section.setFields(fields);
//        form.setSections(sections);
//        int position = 0;
//        assertThat(incidentRegisterPresenter.getFields(position).contains(miniFormField),is
// (true));
//        assertThat(incidentRegisterPresenter.getFields(position).contains(otherFormField),is
// (true));
//    }
//
//    @Test
//    public void should_return_null_when_position_is_known_and_form_is_null(){
//        when(incidentFormService.getGBVTemplate()).thenReturn(null);
//        List<Field> fieldList = incidentRegisterPresenter.getFields(0);
//        assertEquals(fieldList,null);
//    }

}