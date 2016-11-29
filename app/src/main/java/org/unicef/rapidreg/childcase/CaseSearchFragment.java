package org.unicef.rapidreg.childcase;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.unicef.rapidreg.base.RecordSearchFragment;
import org.unicef.rapidreg.base.RecordSearchPresenter;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.service.CaseService;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class CaseSearchFragment extends RecordSearchFragment {

    @Inject
    CaseSearchPresenter caseSearchPresenter;

    @Override
    public RecordSearchPresenter createPresenter() {
        return caseSearchPresenter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getComponent().inject(this);
        return super.onCreateView(inflater, container, savedInstanceState);
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

        return caseSearchPresenter.getSearchResult(shortId, name, ageFrom, ageTo, caregiver, registrationDate);
    }
}


