package org.unicef.rapidreg.childcase.caselist;

import org.unicef.rapidreg.base.record.recordlist.RecordListPresenter;
import org.unicef.rapidreg.base.record.recordlist.spinner.SpinnerState;
import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.service.RecordService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static org.unicef.rapidreg.base.record.recordlist.RecordListFragment.*;

public class CaseListPresenter extends RecordListPresenter {
    private CaseService caseService;
    private CaseFormService caseFormService;

    @Inject
    public CaseListPresenter(CaseService caseService, CaseFormService caseFormService, RecordService recordService) {
        super(recordService);
        this.caseService = caseService;
        this.caseFormService = caseFormService;
    }

    @Override
    public boolean isFormReady() {
        return caseFormService.isReady();
    }

    @Override
    public List<Long> getRecordsByFilter(SpinnerState spinnerState) {
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

    @Override
    public int calculateDisplayedIndex() {
        List<Case> cases = caseService.getAll();
        return cases.isEmpty() ? HAVE_NO_RESULT : HAVE_RESULT_LIST;
    }

    @Override
    public int getsyncedRecordsCount() {
        return caseService.getAllSyncedRecordsId().size();
    }
}
