package org.unicef.rapidreg.childcase;

import android.text.TextUtils;

import org.unicef.rapidreg.base.CaseListPresenter;
import org.unicef.rapidreg.base.RecordListPresenter;
import org.unicef.rapidreg.base.RecordSearchFragment;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.service.CaseService;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class CaseSearchFragment extends RecordSearchFragment {

    @Inject
    CaseListPresenter caseListPresenter;

    @Override
    public RecordListPresenter createPresenter() {
        getComponent().inject(this);
        return caseListPresenter;
    }

    @Override
    protected List<Long> getSearchResult(Map<String, String> filters) {
        String shortId = filters.get(ID);
        String name = filters.get(NAME);
        String from = filters.get(AGE_FROM);
        int ageFrom = TextUtils.isEmpty(from) ? RecordModel.MIN_AGE : Integer.valueOf(from);
        String to = filters.get(AGE_TO);
        int ageTo = TextUtils.isEmpty(to) ? RecordModel.MAX_AGE : Integer.valueOf(to);
        String caregiver = filters.get(CAREGIVER);
        String registrationDate = filters.get(REGISTRATION_DATE);

        return CaseService.getInstance().getSearchResult(shortId, name, ageFrom, ageTo,
                caregiver, getDate(registrationDate));
    }
}


