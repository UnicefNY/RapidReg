package org.unicef.rapidreg.childcase.caselist;

import android.content.Context;

import org.unicef.rapidreg.base.record.recordlist.RecordListFragment;
import org.unicef.rapidreg.base.record.recordlist.RecordListPresenter;
import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.service.RecordService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class CaseListPresenter extends RecordListPresenter {
    private CaseService caseService;
    private CaseFormService caseFormService;

    @Inject
    public CaseListPresenter(CaseService caseService, CaseFormService caseFormService, RecordService recordService) {
        super( recordService);
        this.caseService = caseService;
        this.caseFormService = caseFormService;
    }


    @Override
    public void initView(Context context) {
        if (isViewAttached()) {
            getView().initView(new CaseListAdapter(context));
        }
    }

    public boolean isFormReady() {
        return caseFormService.isFormReady();
    }

    public List<Case> getCases() {
        return caseService.getAll();
    }

    public List<Long> getCasesByFilter(RecordListFragment.SpinnerState spinnerState) {
        switch (spinnerState) {
            case AGE_ASC:
                return caseService.getAllOrderByAgeASC();
            case AGE_DES:
                return caseService.getAllOrderByAgeDES();
            case REG_DATE_ASC:
                return caseService.getAllOrderByDateASC();
            case REG_DATE_DES:
                return caseService.getAllOrderByDateDES();
            default:
                return new ArrayList<>();
        }
    }
}
