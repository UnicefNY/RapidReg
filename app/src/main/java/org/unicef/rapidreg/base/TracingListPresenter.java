package org.unicef.rapidreg.base;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;

import org.unicef.rapidreg.model.Tracing;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.TracingService;
import org.unicef.rapidreg.tracing.TracingListAdapter;
import org.unicef.rapidreg.tracing.TracingListFragment;

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
