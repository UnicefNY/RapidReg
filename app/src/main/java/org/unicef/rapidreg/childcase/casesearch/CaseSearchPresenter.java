package org.unicef.rapidreg.childcase.casesearch;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.base.record.recordsearch.RecordSearchPresenter;
import org.unicef.rapidreg.model.User;
import org.unicef.rapidreg.service.CaseService;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import static org.unicef.rapidreg.base.record.recordsearch.RecordSearchPresenter.CONSTANT.*;

public class CaseSearchPresenter extends RecordSearchPresenter {

    private CaseService caseService;

    @Inject
    public CaseSearchPresenter(CaseService caseService) {
        this.caseService = caseService;
    }

    @Override
    protected List<Long> getSearchResult(Map<String, String> searchConditions) {
        String uniqueId = searchConditions.get(ID);
        String name = searchConditions.get(NAME);
        String registrationDate = searchConditions.get(REGISTRATION_DATE);
        if (PrimeroAppConfiguration.getCurrentUser().getRoleType() == User.Role.CP) {
            int ageFrom = Integer.valueOf(searchConditions.get(AGE_FROM));
            int ageTo = Integer.valueOf(searchConditions.get(AGE_TO));
            String caregiver = searchConditions.get(CAREGIVER);
            return caseService.getCPSearchResult(uniqueId, name, ageFrom, ageTo, caregiver, getDate(registrationDate));
        } else {
            String location = searchConditions.get(LOCATION);
            return caseService.getGBVSearchResult(uniqueId, name, location, getDate(registrationDate));
        }
    }
}
