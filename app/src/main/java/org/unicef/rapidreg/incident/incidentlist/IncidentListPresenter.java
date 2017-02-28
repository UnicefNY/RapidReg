package org.unicef.rapidreg.incident.incidentlist;

import org.unicef.rapidreg.base.record.recordlist.RecordListPresenter;
import org.unicef.rapidreg.base.record.recordlist.spinner.SpinnerState;
import org.unicef.rapidreg.model.Incident;
import org.unicef.rapidreg.service.IncidentFormService;
import org.unicef.rapidreg.service.IncidentService;
import org.unicef.rapidreg.service.RecordService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static org.unicef.rapidreg.base.record.recordlist.RecordListFragment.HAVE_NO_RESULT;
import static org.unicef.rapidreg.base.record.recordlist.RecordListFragment.HAVE_RESULT_LIST;

public class IncidentListPresenter extends RecordListPresenter {
    private IncidentService incidentService;
    private IncidentFormService incidentFormService;

    @Inject
    public IncidentListPresenter(IncidentService incidentService, IncidentFormService
            incidentFormService, RecordService recordService) {
        super(recordService);
        this.incidentService = incidentService;
        this.incidentFormService = incidentFormService;
    }

    @Override
    public boolean isFormReady() {
        return incidentFormService.isReady();
    }

    @Override
    public List<Long> getRecordsByFilter(SpinnerState spinnerState) {
        switch (spinnerState) {
            case AGE_ASC:
                return incidentService.getAllOrderByAgeASC();
            case AGE_DES:
                return incidentService.getAllOrderByAgeDES();
            case INTERVIEW_DATE_ASC:
                return incidentService.getAllOrderByDateASC();
            case INTERVIEW_DATE_DES:
                return incidentService.getAllOrderByDateDES();
            default:
                return new ArrayList<>();
        }
    }

    @Override
    public List<Long> getSyncedRecords() {
        return incidentService.getAllSyncedRecordsId();
    }

    @Override
    public int getsyncedRecordsCount() {
        return incidentService.getAllSyncedRecordsId().size();
    }

    @Override
    public int calculateDisplayedIndex() {
        List<Incident> incidents = incidentService.getAll();
        return incidents.isEmpty() ? HAVE_NO_RESULT : HAVE_RESULT_LIST;
    }
}
