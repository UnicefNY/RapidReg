package org.unicef.rapidreg.incident.incidentsearch;

import org.unicef.rapidreg.base.record.recordsearch.RecordSearchPresenter;
import org.unicef.rapidreg.service.IncidentService;

import java.util.List;

import javax.inject.Inject;

public class IncidentSearchPresenter extends RecordSearchPresenter {

    private IncidentService incidentService;

    @Inject
    public IncidentSearchPresenter(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @Override
    public List<Long> getSearchResult(String shortId, String name, int ageFrom, int ageTo,
                                         String caregiver, String registrationDate) {
        return incidentService.getSearchResult(shortId, name, ageFrom, ageTo, getDate
                (registrationDate));
    }

}