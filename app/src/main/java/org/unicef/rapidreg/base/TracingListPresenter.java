package org.unicef.rapidreg.base;

import android.content.Context;

import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.tracing.TracingListAdapter;

import javax.inject.Inject;

public class TracingListPresenter extends RecordListPresenter {

    @Inject
    public TracingListPresenter(CaseService caseService, CaseFormService caseFormService, RecordService recordService) {
        super(caseService, caseFormService, recordService);
    }

    @Override
    public void initView(Context context) {
        if (isViewAttached()) {
            getView().initView(new TracingListAdapter(context));
        }
    }


}
