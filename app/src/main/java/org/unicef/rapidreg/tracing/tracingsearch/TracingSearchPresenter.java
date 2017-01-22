package org.unicef.rapidreg.tracing.tracingsearch;

import org.unicef.rapidreg.base.record.recordsearch.RecordSearchPresenter;
import org.unicef.rapidreg.service.TracingService;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class TracingSearchPresenter extends RecordSearchPresenter {
    private TracingService tracingService;

    @Inject
    public TracingSearchPresenter(TracingService tracingService) {
        this.tracingService = tracingService;
    }

    @Override
    protected List<Long> getSearchResult(Map<String, String> searchConditions) {
        String id = searchConditions.get(ID);
        String name = searchConditions.get(NAME);
        int ageFrom = Integer.valueOf(searchConditions.get(AGE_FROM));
        int ageTo = Integer.valueOf(searchConditions.get(AGE_TO));
        String dateOfInquiry = searchConditions.get(DATE_OF_INQUIRY);

        return tracingService.getSearchResult(id, name, ageFrom, ageTo, getDate(dateOfInquiry));
    }
}
