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
        idField.setVisibility(View.VISIBLE);
        survivorCodeField.setVisibility(View.VISIBLE);
        nameField.setVisibility(View.VISIBLE);
        ageField.setVisibility(View.VISIBLE);
        typeOfViolenceField.setVisibility(View.VISIBLE);
        locationField.setVisibility(View.VISIBLE);

        initTypeOfViolenceField();
    }

    private void initTypeOfViolenceField() {
        final List<String> typeOfViolenceVals = incidentSearchPresenter.getViolenceTypeList();
        typeOfViolence.setAdapter(generateTypeOfViolenceListAdapter(typeOfViolenceVals));

        typeOfViolence.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                typeOfViolenceVal = typeOfViolenceVals.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                typeOfViolenceVal = "";
            }
        });
    }

    private SpinnerAdapter generateTypeOfViolenceListAdapter(List<String> violenceTypeList) {
        StringSpinnerAdapter adapter = new StringSpinnerAdapter(getActivity(), R.layout.string_list_spinner_opened, violenceTypeList);
        return adapter;
    }

    @Override
    protected RecordListAdapter createRecordListAdapter() {
        return incidentListAdapter;
    }

}
