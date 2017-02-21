package org.unicef.rapidreg.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.unicef.rapidreg.repository.CasePhotoDao;
import org.unicef.rapidreg.model.CasePhoto;

import java.util.Collections;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CasePhotoServiceTest {
    @Mock
    CasePhotoDao casePhotoDao;

    @InjectMocks
    CasePhotoService casePhotoService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void should_call_get_by_id_of_case_photo_dao() throws Exception {
        CasePhoto casePhoto = new CasePhoto();
        when(casePhotoDao.getById(anyLong())).thenReturn(casePhoto);

        assertThat("Should return CasePhoto", casePhotoService.getById(123L), is(casePhoto));
        verify(casePhotoDao, times(1)).getById(123L);
    }

    @Test
    public void should_call_get_first_of_case_photo_dao() throws Exception {
        when(casePhotoDao.getFirst(anyLong())).thenReturn(new CasePhoto());

        casePhotoService.getFirst(123L);
        verify(casePhotoDao, times(1)).getFirst(123L);
    }

    @Test
    public void should_call_get_ids_by_case_id_of_case_photo_dao() throws Exception {
        when(casePhotoDao.getIdsByCaseId(anyLong())).thenReturn(Collections.EMPTY_LIST);

        casePhotoService.getIdsByCaseId(123L);
        verify(casePhotoDao, times(1)).getIdsByCaseId(123L);
    }

    @Test
    public void should_call_count_un_synced_by_case_photo_dao() throws Exception {
        when(casePhotoDao.countUnSynced(anyLong())).thenReturn(1L);

        boolean actual = casePhotoService.hasUnSynced(123L);

        verify(casePhotoDao, times(1)).countUnSynced(123L);
        assertTrue("When hasUnSynced() is more than zero, should return true", actual);
    }

    @Test
    public void should_return_false_when_count_un_synced_less_then_zero() throws Exception {
        when(casePhotoDao.countUnSynced(anyLong())).thenReturn(-1L);
        assertThat("Should return false", casePhotoService.hasUnSynced(1L), is(false));
        verify(casePhotoDao).countUnSynced(1L);
    }

    @Test
    public void should_call_delete_by_case_id_in_case_photo_dao() throws Exception {
        casePhotoService.deleteByCaseId(123L);

        verify(casePhotoDao, times(1)).deleteByCaseId(123L);
    }
}