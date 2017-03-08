package org.unicef.rapidreg.tracing.tracinglist;

import org.unicef.rapidreg.base.record.recordlist.RecordListFragment;
import org.unicef.rapidreg.base.record.recordlist.RecordListPresenter;
import org.unicef.rapidreg.base.record.recordlist.spinner.SpinnerState;
import org.unicef.rapidreg.model.Tracing;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.TracingFormService;
import org.unicef.rapidreg.service.TracingService;

import java.util.List;

import javax.inject.Inject;

import static org.unicef.rapidreg.base.record.recordlist.RecordListFragment.*;


public class TracingListPresenter extends RecordListPresenter {

    private TracingService tracingService;
    private TracingFormService tracingFormService;

    @Inject
    public TracingListPresenter(TracingService tracingService, TracingFormService tracingFormService, RecordService recordService) {
        super(recordService);
        this.tracingService = tracingService;
        this.tracingFormService = tracingFormService;
    }

    @Override
    public boolean isFormReady() {
        return tracingFormService.isReady();
    }

    @Override
    public int calculateDisplayedIndex() {
        List<Tracing> tracings = tracingService.getAll();
        return tracings.isEmpty() ? HAVE_NO_RESULT : RecordListFragment.HAVE_RESULT_LIST;
    }

    @Override
    public List<Long> getRecordsByFilter(SpinnerState spinnerState) {
        switch (spinnerState) {
            case INQUIRY_DATE_ASC:
                return tracingService.getAllOrderByDateASC();
            case INQUIRY_DATE_DES:
                return tracingService.getAllOrderByDateDES();
            default:
                return null;
        }
    }

    @Override
    public List<Long> getSyncedRecords() {
        return tracingService.getAllSyncedRecordsId();
    }

    @Override
    public int getSyncedRecordsCount() {
        return tracingService.getAllSyncedRecordsId().size();
    }
}
