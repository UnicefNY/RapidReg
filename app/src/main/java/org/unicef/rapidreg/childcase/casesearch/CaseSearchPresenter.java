package org.unicef.rapidreg.childcase.casesearch;

import android.content.Context;

import org.unicef.rapidreg.base.record.recordsearch.RecordSearchPresenter;
import org.unicef.rapidreg.childcase.caselist.CaseListAdapter;
import org.unicef.rapidreg.service.CaseService;

import java.util.List;

import javax.inject.Inject;

public class CaseSearchPresenter extends RecordSearchPresenter {

    private CaseService caseService;

    @Inject
    public CaseSearchPresenter(CaseService caseService) {
        this.caseService = caseService;
    }

    @Override
    public List<Long> getSearchResult(String shortId, String name, int ageFrom, int ageTo, String caregiver, String registrationDate) {
        return caseService.getSearchResult(shortId, name, ageFrom, ageTo, caregiver, getDate(registrationDate));
    }
}
