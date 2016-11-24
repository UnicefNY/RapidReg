package org.unicef.rapidreg.base;

import android.content.Context;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import org.unicef.rapidreg.base.RecordListFragment.SpinnerState;
import org.unicef.rapidreg.childcase.CaseListAdapter;
import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.tracing.TracingListAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class RecordListPresenter extends MvpBasePresenter<RecordListView> {

    @Inject
    CaseService caseService;

    @Inject
    CaseFormService caseFormService;

    @Inject
    RecordService recordService;

    private int type;

    @Inject
    public RecordListPresenter(CaseService caseService, CaseFormService caseFormService, RecordService recordService) {
        this.caseService = caseService;
        this.caseFormService = caseFormService;
        this.recordService = recordService;
    }

    public RecordListPresenter(int type) {
        this.type = type;
    }

    public void initView(Context context) {
        if (isViewAttached()) {
            if (type == RecordModel.CASE) {
                getView().initView(new CaseListAdapter(context));
            } else if (type == RecordModel.TRACING) {
                getView().initView(new TracingListAdapter(context));
            }
        }
    }


    public List<Case> getCases() {
        return caseService.getAll();
    }

    public List<Long> getCasesByFilter(SpinnerState spinnerState) {
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

    public void clearAudioFile() {
        recordService.clearAudioFile();
    }

    public boolean isFormReady() {
        return caseFormService.isFormReady();
    }
}
