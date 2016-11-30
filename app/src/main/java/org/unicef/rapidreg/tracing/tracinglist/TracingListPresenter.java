package org.unicef.rapidreg.tracing.tracinglist;

import android.content.Context;

import org.unicef.rapidreg.base.RecordListFragment;
import org.unicef.rapidreg.base.RecordListPresenter;
import org.unicef.rapidreg.model.Tracing;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.TracingService;

import java.util.List;

import javax.inject.Inject;


public class TracingListPresenter extends RecordListPresenter {

    private TracingService tracingService;

    @Inject
    public TracingListPresenter(RecordService recordService, TracingService tracingService) {
        super(recordService);
        this.tracingService = tracingService;
    }

    @Override
    public void initView(Context context) {
        if (isViewAttached()) {
            getView().initView(new TracingListAdapter(context, tracingService));
        }
    }


    public int calculateDisplayedIndex() {
        List<Tracing> tracings = tracingService.getAll();
        int index = tracings.isEmpty() ? RecordListFragment.HAVE_NO_RESULT : RecordListFragment.HAVE_RESULT_LIST;
        return index;
    }

    public List<Long> getRecordOrders(int position) {
        switch (TracingListFragment.SPINNER_STATES[position]) {
            case INQUIRY_DATE_ASC:
                return tracingService.getAllOrderByDateASC();
            case INQUIRY_DATE_DES:
                return tracingService.getAllOrderByDateDES();
            default:
                return null;
        }
    }
}
