package org.unicef.rapidreg.base;

import android.content.Context;

import org.unicef.rapidreg.childcase.CaseListAdapter;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.service.RecordService;

import javax.inject.Inject;

public class CaseListPresenter extends RecordListPresenter {

    @Inject
    public CaseListPresenter(CaseService caseService, CaseFormService caseFormService, RecordService recordService) {
        super(caseService, caseFormService, recordService);
    }

    @Override
    public void initView(Context context) {
        if (isViewAttached()) {
            getView().initView(new CaseListAdapter(context));
        }
    }
}
