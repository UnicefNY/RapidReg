package org.unicef.rapidreg.tracing.tracingsearch;

import org.unicef.rapidreg.base.record.recordsearch.RecordSearchPresenter;
import org.unicef.rapidreg.service.TracingService;

import java.util.List;

import javax.inject.Inject;

public class TracingSearchPresenter extends RecordSearchPresenter {
    private TracingService tracingService;

    @Inject
    public TracingSearchPresenter(TracingService tracingService) {
        this.tracingService = tracingService;
    }

    @Override
    protected List<Long> getSearchResult(String shortId, String name, int ageFrom, int ageTo,
                                         String caregiver, String registrationDate) {
        return tracingService.getSearchResult(shortId, name, ageFrom, ageTo, getDate
                (registrationDate));
    }
}
