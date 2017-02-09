package org.unicef.rapidreg.service;

import com.raizlabs.android.dbflow.data.Blob;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;

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
import org.unicef.rapidreg.model.User;
import org.unicef.rapidreg.repository.CaseDao;
import org.unicef.rapidreg.repository.CasePhotoDao;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.utils.TextUtils;
import org.unicef.rapidreg.utils.Utils;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import edu.emory.mathcs.backport.java.util.Arrays;

import static junit.framework.Assert.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.unicef.rapidreg.service.CaseService.CASE_ID;
import static org.unicef.rapidreg.service.RecordService.AGE;
import static org.unicef.rapidreg.service.RecordService.REGISTRATION_DATE;

@RunWith(PowerMockRunner.class)
@PrepareForTest({UUID.class, PrimeroAppConfiguration.class})
public class CaseServiceTest {

    @Mock
    CaseDao caseDao;

    @Mock
    CasePhotoDao casePhotoDao;

    @InjectMocks
    CaseService caseService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        PowerMockito.mockStatic(UUID.class);
        PowerMockito.mockStatic(PrimeroAppConfiguration.class);

        UUID uuid = mock(UUID.class);
        when(uuid.toString()).thenReturn("anuuidwhichlengthis21");
        when(UUID.randomUUID()).thenReturn(uuid);

        User user = new User("primero");
        Mockito.when(PrimeroAppConfiguration.getCurrentUser()).thenReturn(user);
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
        String url = "https://35.61.56.113:8443";
        Mockito.when(PrimeroAppConfiguration.getApiBaseUrl()).thenReturn(url);
        CaseService caseServiceSpy = spy(caseService);
        String uuid = UUID.randomUUID().toString();
        when(caseServiceSpy.generateUniqueId()).thenReturn(uuid);
        ItemValuesMap itemValues = new ItemValuesMap();
        Case actual = caseServiceSpy.save(itemValues, Collections.EMPTY_LIST);
        when(caseDao.save(any(Case.class))).thenReturn(actual);

        assertThat("Should have save url", actual.getServerUrl(), is(TextUtils.lintUrl(url)));
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

        assertFalse("Sync status should be false", actual.isSynced());
        assertThat("Age should be 18", actual.getAge(), is(18));
        assertThat("Registration date should be 25/12/2016", actual.getRegistrationDate(), is
                (Utils.getRegisterDate("25/12/2016")));
    }

    @Test
    public void should_get_search_result_by_condition_group() throws Exception {
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

        List<Long> actual = caseService.getCPSearchResult("shortId", "name", 0, 10, "caregiver",
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
}
