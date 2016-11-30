package org.unicef.rapidreg.tracing.tracingsearch;

import android.content.Context;

import org.unicef.rapidreg.base.record.recordsearch.RecordSearchPresenter;
import org.unicef.rapidreg.service.TracingService;
import org.unicef.rapidreg.tracing.tracinglist.TracingListAdapter;

import java.util.List;
import javax.inject.Inject;

public class TracingSearchPresenter extends RecordSearchPresenter {
    private TracingService tracingService;

    @Inject
    public TracingSearchPresenter(TracingService tracingService) {
        this.tracingService = tracingService;
    }

    @Override
    public void initView(Context context) {
        if (isViewAttached()) {
            getView().initView(new TracingListAdapter(context, tracingService));
        }
    }

    public List<Long> getSearchResult(String id, String name, int ageFrom, int ageTo, String registrationDate) {
        return tracingService.getSearchResult(id, name, ageFrom, ageTo, getDate(registrationDate));
    }
}
