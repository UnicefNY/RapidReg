package org.unicef.rapidreg.childcase.casesearch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.unicef.rapidreg.PrimeroConfiguration;
import org.unicef.rapidreg.base.record.recordlist.RecordListAdapter;
import org.unicef.rapidreg.base.record.recordsearch.RecordSearchFragment;
import org.unicef.rapidreg.base.record.recordsearch.RecordSearchPresenter;
import org.unicef.rapidreg.childcase.caselist.CaseListAdapter;
import org.unicef.rapidreg.model.User;

import javax.inject.Inject;

public class CaseSearchFragment extends RecordSearchFragment {

    @Inject
    CaseSearchPresenter caseSearchPresenter;

    @Inject
    CaseListAdapter caseListAdapter;

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
    protected void onInitSearchFields() {
        initSearchCondition(PrimeroConfiguration.getCurrentUser().getRoleType());
    }

    private void initSearchCondition(User.Role roleType) {
        switch (roleType) {
            case CP: locationField.setVisibility(View.GONE); break;
            case GBV:
                ageField.setVisibility(View.GONE);
                caregiverField.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    protected RecordListAdapter createRecordListAdapter() {
        return caseListAdapter;
    }
}


