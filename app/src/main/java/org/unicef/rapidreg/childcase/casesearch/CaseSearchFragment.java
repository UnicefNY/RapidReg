package org.unicef.rapidreg.childcase.casesearch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.unicef.rapidreg.base.record.recordlist.RecordListAdapter;
import org.unicef.rapidreg.base.record.recordsearch.RecordSearchFragment;
import org.unicef.rapidreg.base.record.recordsearch.RecordSearchPresenter;
import org.unicef.rapidreg.childcase.caselist.CaseListAdapter;
import org.unicef.rapidreg.model.RecordModel;

import java.util.List;
import java.util.Map;

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
    protected RecordListAdapter createRecordListAdapter() {
        return caseListAdapter;
    }
}


