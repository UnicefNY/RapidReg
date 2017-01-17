package org.unicef.rapidreg.incident.incidentsearch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.unicef.rapidreg.base.record.recordlist.RecordListAdapter;
import org.unicef.rapidreg.base.record.recordsearch.RecordSearchFragment;
import org.unicef.rapidreg.base.record.recordsearch.RecordSearchPresenter;
import org.unicef.rapidreg.incident.incidentlist.IncidentListAdapter;

import javax.inject.Inject;

public class IncidentSearchFragment extends RecordSearchFragment {

    @Inject
    IncidentSearchPresenter incidentSearchPresenter;

    @Inject
    IncidentListAdapter incidentListAdapter;

    @Override
    public RecordSearchPresenter createPresenter() {
        return incidentSearchPresenter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        getComponent().inject(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void onInitSearchFields() {

    }

    @Override
    protected RecordListAdapter createRecordListAdapter() {
        return incidentListAdapter;
    }

}
