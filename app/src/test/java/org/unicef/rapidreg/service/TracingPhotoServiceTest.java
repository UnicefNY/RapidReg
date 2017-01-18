package org.unicef.rapidreg.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.unicef.rapidreg.repository.TracingPhotoDao;
import org.unicef.rapidreg.model.TracingPhoto;

import java.util.Collections;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TracingPhotoServiceTest {

    @Mock
    TracingPhotoDao tracingPhotoDao;

    @InjectMocks
    TracingPhotoService tracingPhotoService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void should_call_get_by_id_of_tracing_photo_dao() throws Exception {
        when(tracingPhotoDao.getById(anyLong())).thenReturn(new TracingPhoto());

        tracingPhotoService.getById(123L);
        verify(tracingPhotoDao, times(1)).getById(123L);
    }

    @Test
    public void should_call_get_first_of_tracing_photo_dao() throws Exception {
        when(tracingPhotoDao.getFirst(anyLong())).thenReturn(new TracingPhoto());

        tracingPhotoService.getFirst(123L);
        verify(tracingPhotoDao, times(1)).getFirst(123L);
    }

    @Test
    public void should_call_get_ids_by_tracing_id_of_tracing_photo_dao() throws Exception {
        when(tracingPhotoDao.getIdsByTracingId(anyLong())).thenReturn(Collections.EMPTY_LIST);

        tracingPhotoService.getIdsByTracingId(123L);
        verify(tracingPhotoDao, times(1)).getIdsByTracingId(123L);
    }

    @Test
    public void should_call_count_un_synced_by_tracing_photo_dao() throws Exception {
        when(tracingPhotoDao.countUnSynced(anyLong())).thenReturn(1L);

        boolean actual = tracingPhotoService.hasUnSynced(123L);

        verify(tracingPhotoDao, times(1)).countUnSynced(123L);
        assertTrue("When hasUnSynced() is more than zero, should return true", actual);
    }

    @Test
    public void should_call_delete_by_tracing_id_in_tracing_photo_dao() throws Exception {
        tracingPhotoService.deleteByTracingId(123L);

        verify(tracingPhotoDao, times(1)).deleteByTracingId(123L);
    }

}