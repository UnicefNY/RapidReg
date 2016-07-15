package org.unicef.rapidreg.childcase;

import android.text.TextUtils;

import org.unicef.rapidreg.base.RecordListPresenter;
import org.unicef.rapidreg.base.RecordSearchFragment;
import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.service.CaseService;

import java.util.List;
import java.util.Map;

public class CaseSearchFragment extends RecordSearchFragment {
    @Override
    public RecordListPresenter createPresenter() {
        return new RecordListPresenter(RecordModel.CASE);
    }

    @Override
    protected List<Case> getSearchResult(Map<String, String> filters) {
        String id = filters.get(ID);
        String name = filters.get(NAME);
        String from = filters.get(AGE_FROM);
        int ageFrom = TextUtils.isEmpty(from) ? RecordModel.MIN_AGE : Integer.valueOf(from);
        String to = filters.get(AGE_TO);
        int ageTo = TextUtils.isEmpty(to) ? RecordModel.MAX_AGE : Integer.valueOf(to);
        String caregiver = filters.get(CAREGIVER);
        String registrationDate = filters.get(REGISTRATION_DATE);

        return CaseService.getInstance().getSearchResult(id, name, ageFrom, ageTo,
                caregiver, getDate(registrationDate));
    }
}


