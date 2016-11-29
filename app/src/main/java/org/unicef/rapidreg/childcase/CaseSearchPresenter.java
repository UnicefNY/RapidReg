package org.unicef.rapidreg.childcase;

import org.unicef.rapidreg.base.RecordSearchPresenter;
import org.unicef.rapidreg.service.CaseService;

import java.util.List;

import javax.inject.Inject;

public class CaseSearchPresenter extends RecordSearchPresenter {

    private CaseService caseService;

    @Inject
    public CaseSearchPresenter(CaseService caseService) {
        this.caseService = caseService;
    }

    public List<Long> getSearchResult(String shortId, String name, int ageFrom, int ageTo, String caregiver, String registrationDate) {
        return caseService.getSearchResult(shortId, name, ageFrom, ageTo, caregiver, getDate(registrationDate));
    }
}