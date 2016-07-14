package org.unicef.rapidreg.tracing;

import android.text.TextUtils;

import org.unicef.rapidreg.base.RecordListPresenter;
import org.unicef.rapidreg.base.RecordSearchFragment;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.model.Tracing;
import org.unicef.rapidreg.service.TracingService;

import java.util.List;
import java.util.Map;

public class TracingSearchFragment extends RecordSearchFragment {
    @Override
    public RecordListPresenter createPresenter() {
        return new RecordListPresenter(RecordModel.TRACING);
    }

    @Override
    protected List<Tracing> getSearchResult(Map<String, String> filters) {
        String id = filters.get(ID);
        String name = filters.get(NAME);
        String from = filters.get(AGE_FROM);
        int ageFrom = TextUtils.isEmpty(from) ? RecordModel.MIN_AGE : Integer.valueOf(from);
        String to = filters.get(AGE_TO);
        int ageTo = TextUtils.isEmpty(to) ? RecordModel.MAX_AGE : Integer.valueOf(to);
        String caregiver = filters.get(CAREGIVER);
        String registrationDate = filters.get(REGISTRATION_DATE);

        return TracingService.getInstance().getSearchResult(id, name, ageFrom, ageTo,
                caregiver, getDate(registrationDate));
    }
}
