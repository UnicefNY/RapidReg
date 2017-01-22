package org.unicef.rapidreg.incident.incidentsearch;

import org.unicef.rapidreg.base.record.recordsearch.RecordSearchPresenter;
import org.unicef.rapidreg.injection.PerFragment;
import org.unicef.rapidreg.service.IncidentFormService;
import org.unicef.rapidreg.service.IncidentService;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import static org.unicef.rapidreg.base.record.recordsearch.RecordSearchPresenter.CONSTANT.*;

@PerFragment
public class IncidentSearchPresenter extends RecordSearchPresenter {

    private IncidentService incidentService;
    private IncidentFormService incidentFormService;

    @Inject
    public IncidentSearchPresenter(IncidentService incidentService, IncidentFormService incidentFormService) {
        this.incidentService = incidentService;
        this.incidentFormService = incidentFormService;
    }

    @Override
    protected List<Long> getSearchResult(Map<String, String> searchConditions) {
        String id = searchConditions.get(ID);
        String survivorCode = searchConditions.get(SURVIVOR_CODE);
        int ageFrom = Integer.valueOf(searchConditions.get(AGE_FROM));
        int ageTo = Integer.valueOf(searchConditions.get(AGE_TO));
        String typeOfViolence = searchConditions.get(TYPE_OF_VIOLENCE);
        return incidentService.getSearchResult(id, survivorCode, ageFrom, ageTo, typeOfViolence, "");
    }

    public List<String> getViolenceTypeList() {
        return incidentFormService.getViolenceTypeList();
    }
}