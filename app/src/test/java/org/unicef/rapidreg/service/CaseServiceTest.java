package org.unicef.rapidreg.service;

import android.util.Log;

import com.raizlabs.android.dbflow.data.Blob;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;

import org.hamcrest.Matchers;
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
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.forms.Section;
import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.model.CasePhoto;
import org.unicef.rapidreg.model.Incident;
import org.unicef.rapidreg.model.User;
import org.unicef.rapidreg.repository.CaseDao;
import org.unicef.rapidreg.repository.CasePhotoDao;
import org.unicef.rapidreg.repository.IncidentDao;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.utils.ImageCompressUtil;
import org.unicef.rapidreg.utils.TextUtils;
import org.unicef.rapidreg.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import edu.emory.mathcs.backport.java.util.Arrays;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.unicef.rapidreg.service.CaseService.CASE_ID;
import static org.unicef.rapidreg.service.RecordService.AGE;
import static org.unicef.rapidreg.service.RecordService.REGISTRATION_DATE;

@RunWith(PowerMockRunner.class)
@PrepareForTest({UUID.class, PrimeroAppConfiguration.class,
        ImageCompressUtil.class, Log.class})
public class CaseServiceTest {

    @Mock
    CaseDao caseDao;

    @Mock
    CasePhotoDao casePhotoDao;

    @Mock
    IncidentDao incidentDao;

    @InjectMocks
    CaseService caseService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        PowerMockito.mockStatic(UUID.class);
        PowerMockito.mockStatic(PrimeroAppConfiguration.class);
        PowerMockito.mockStatic(ImageCompressUtil.class);
        PowerMockito.mockStatic(Log.class);

        UUID uuid = mock(UUID.class);
        when(uuid.toString()).thenReturn("anuuidwhichlengthis21");
        when(UUID.randomUUID()).thenReturn(uuid);

        User user = new User("primero");
        Mockito.when(PrimeroAppConfiguration.getCurrentUser()).thenReturn(user);
        String url = "https://35.61.56.113:8443";
        Mockito.when(PrimeroAppConfiguration.getApiBaseUrl()).thenReturn(url);
    }

    @Test
    public void should_get_required_filed_list_when_exist_in_case_fields() {
        List<Field> fields = new Section().getFields();
        fields.add(makeCaseField("age", true));
        fields.add(makeCaseField("sex", true));
        fields.add(makeCaseField("name", false));

        List<String> requiredFiledNames = caseService.fetchRequiredFiledNames(fields);
        assertThat(requiredFiledNames, hasSize(2));
        assertThat(requiredFiledNames, containsInAnyOrder("sex", "age"));
    }

    @Test
    public void should_get_empty_required_filed_list_when_does_not_exist_in_case_fields() {
        List<Field> fields = new Section().getFields();
        fields.add(makeCaseField("age", false));
        fields.add(makeCaseField("sex", false));
        fields.add(makeCaseField("name", false));

        List<String> requiredFiledNames = caseService.fetchRequiredFiledNames(fields);
        assertThat(requiredFiledNames, hasSize(0));
    }

    @Test
    public void should_save_case_when_give_item_values() throws Exception {
        CaseService caseServiceSpy = spy(caseService);
        String uuid = UUID.randomUUID().toString();
        when(caseServiceSpy.generateUniqueId()).thenReturn(uuid);

        ItemValuesMap itemValues = new ItemValuesMap();
        Case actual = caseServiceSpy.save(itemValues, Collections.EMPTY_LIST);
        when(caseDao.save(any(Case.class))).thenReturn(actual);

        assertThat("Should have save url", actual.getServerUrl(), is(TextUtils.lintUrl("https://35.61.56.113:8443")));
        assertThat("Should have save uuid.", actual.getUniqueId(), is(uuid));
        verify(caseDao, times(1)).save(any(Case.class));
    }

    @Test
    public void should_update_case_when_give_item_values() throws Exception {
        ItemValuesMap itemValues = new ItemValuesMap();
        itemValues.addStringItem(CASE_ID, "existedUniqueId");
        itemValues.addNumberItem(AGE, 18);
        itemValues.addStringItem(REGISTRATION_DATE, "25/12/2016");

        Case expected = new Case();
        when(caseDao.getCaseByUniqueId("existedUniqueId")).thenReturn(expected);

        expected.setContent(new Blob(new String("").getBytes()));

        when(caseDao.update(expected)).thenReturn(expected);

        Case actual = caseService.update(itemValues, Collections.EMPTY_LIST);

        verify(caseDao, times(1)).update(actual);
        verify(casePhotoDao, times(1)).getIdsByCaseId(actual.getId());

        assertFalse("Sync status should be false", actual.isSynced());
        assertThat("Age should be 18", actual.getAge(), is(18));
        assertThat("Registration date should be 25/12/2016", actual.getRegistrationDate(), is
                (Utils.getRegisterDate("25/12/2016")));
    }

    @Test
    public void should_get_CP_search_result_by_condition_group() throws Exception {
        Case searchCaseOne = new Case(10000L);
        Case searchCaseTwo = new Case(10001L);
        Case searchCaseThree = new Case(10002L);
        Case searchCaseFour = new Case(10003L);
        List<Case> searchResult = Arrays.asList(new Case[]{
                searchCaseOne,
                searchCaseTwo,
                searchCaseThree,
                searchCaseFour});
        when(caseDao.getCaseListByConditionGroup(anyString(), anyString(), any(ConditionGroup
                .class))).thenReturn
                (searchResult);

        List<Long> actual = caseService.getCPSearchResult("", "", 0, 0, "",
                null);

        assertThat("Should return id list", actual, is(Arrays.asList(new Long[]{10000L, 10001L,
                10002L, 10003L})));
    }

    private Field makeCaseField(String name, boolean required) {
        Field field = new Field();
        field.setRequired(required);
        field.setName(name);
        return field;
    }

    @Test
    public void should_get_null_incidents_ids_when_incidents_is_null() throws Exception {
        when(incidentDao.getAllIncidentsByCaseUniqueId(anyString())).thenReturn(null);
        List<String> incidents = caseService.getIncidentsByCaseId("");
        assertThat("Incidents should be null", incidents, is(nullValue()));
    }

    @Test
    public void should_get_incidents_ids_when_incidents_exits() throws Exception {
        List<Incident> incidents = new ArrayList<>();
        Incident incident = new Incident(10086L);
        incidents.add(incident);
        List<String> listStr = new ArrayList<>();
        CaseService caseServiceSpy = spy(caseService);

        when(incidentDao.getAllIncidentsByCaseUniqueId(anyString())).thenReturn(incidents);
        when(caseServiceSpy.extractUniqueIds(incidents)).thenReturn(listStr);

        assertThat("Should return same incident string list", caseServiceSpy.getIncidentsByCaseId(""), is(listStr));
    }

    @Test
    public void should_get_GBV_search_result_by_condition_group() throws Exception {
        List<Case> searchResult = new ArrayList<>();

        when(caseDao.getCaseListByConditionGroup(anyString(), anyString(), any(ConditionGroup.class)))
                .thenReturn(searchResult);

        List<Long> actual = caseService.getGBVSearchResult("", "", "", null);

        assertThat("Should return id list", actual, is(searchResult));
    }

    @Test
    public void should_call_update_when_case_id_exits() throws Exception {
        ItemValuesMap itemValues = new ItemValuesMap();
        itemValues.addStringItem(CASE_ID, "existedUniqueId");
        itemValues.addNumberItem(AGE, 18);
        itemValues.addStringItem(REGISTRATION_DATE, "25/12/2016");

        Case c = new Case();
        when(caseDao.getCaseByUniqueId(anyString())).thenReturn(c);
        when(caseDao.update(any())).thenReturn(c);

        assertThat("Should return update case", caseService.saveOrUpdate(itemValues, Collections.EMPTY_LIST), is(c));
        verify(caseDao, times(1)).getCaseByUniqueId("existedUniqueId");
        verify(caseDao, times(1)).update(c);
    }

    @Test
    public void should_call_save_when_case_id_is_null() throws Exception {
        ItemValuesMap itemValues = new ItemValuesMap();
        itemValues.addStringItem(CASE_ID, null);

        CaseService caseServiceSpy = spy(caseService);
        String uuid = UUID.randomUUID().toString();
        when(caseServiceSpy.generateUniqueId()).thenReturn(uuid);

        Case aCase = caseServiceSpy.saveOrUpdate(itemValues, Collections.emptyList());

        assertThat("Should return save case", aCase.getUniqueId(), is(uuid));
        verify(caseDao, times(1)).save(any(Case.class));
    }

    @Test
    public void should_save_photo() throws Exception {
        List<String> photoPaths = Arrays.asList(new String[]{"aa"});
        CasePhoto casePhoto = new CasePhoto();

        when(casePhotoDao.getByCaseIdAndOrder(anyLong(), anyInt())).thenReturn(casePhoto);
        when(ImageCompressUtil.readImageFile(anyString())).thenReturn(null);

        caseService.savePhoto(new Case(), photoPaths);
        verify(casePhotoDao, times(1)).save(casePhoto);
    }

    @Test
    public void should_get_case_by_internal_id() throws Exception {
        Case c = new Case();
        when(caseDao.getByInternalId(anyString())).thenReturn(c);
        assertThat("Should get case", caseService.getByInternalId(""), is(c));
    }

    @Test
    public void should_not_has_same_rev_when_case_is_null() throws Exception {
        when(caseDao.getByInternalId(anyString())).thenReturn(null);
        assertThat("Should return false,not same", caseService.hasSameRev("", ""), is(false));
    }

    @Test
    public void should_not_has_same_rev_when_rev_not_equals_case_rev() throws Exception {
        Case c = new Case();
        c.setInternalRev("aa");
        when(caseDao.getByInternalId(anyString())).thenReturn(c);
        assertThat("Should return false, not same", caseService.hasSameRev("", "bb"), is(false));
    }

    @Test
    public void should_has_same_rev() throws Exception {
        Case c = new Case();
        c.setInternalRev("aa");
        when(caseDao.getByInternalId(anyString())).thenReturn(c);
        assertThat("Should return true, same", caseService.hasSameRev("", "aa"), is(true));
    }

    @Test
    public void should_delete_when_case_exists_and_synced_yet() throws Exception {
        long recordId = 123L;

        Case deleteCase = new Case(recordId);
        deleteCase.setSynced(true);
        when(caseDao.getCaseById(recordId)).thenReturn(deleteCase);

        Case actual = caseService.deleteByRecordId(recordId);

        verify(caseDao, times(1)).delete(deleteCase);
        verify(casePhotoDao, times(1)).deleteByCaseId(recordId);
        assertThat(actual, is(deleteCase));
    }

    @Test
    public void should_not_delete_when_case_exist_and_not_synced_yet() throws Exception {
        long recordId = 123L;
        Case deleteCase = new Case(recordId);
        deleteCase.setSynced(false);
        when(caseDao.getCaseById(recordId)).thenReturn(deleteCase);

        Case actual = caseService.deleteByRecordId(recordId);

        verify(caseDao, never()).delete(deleteCase);
        verify(casePhotoDao, never()).deleteByCaseId(recordId);
        assertNull(actual);
    }


}
