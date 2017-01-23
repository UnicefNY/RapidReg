package org.unicef.rapidreg.incident.incidentsearch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.record.recordlist.RecordListAdapter;
import org.unicef.rapidreg.base.record.recordsearch.RecordSearchFragment;
import org.unicef.rapidreg.base.record.recordsearch.RecordSearchPresenter;
import org.unicef.rapidreg.base.record.recordsearch.StringSpinnerAdapter;
import org.unicef.rapidreg.incident.incidentlist.IncidentListAdapter;
import org.unicef.rapidreg.widgets.ClearableEditText;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import static org.unicef.rapidreg.base.record.recordsearch.RecordSearchPresenter.CONSTANT.*;
import static org.unicef.rapidreg.model.RecordModel.EMPTY_AGE;

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
    protected Map<String, String> getFilterValues() {
        Map<String, String> searchValues = new LinkedHashMap<>();
        searchValues.put(ID, id.getText());
        searchValues.put(SURVIVOR_CODE, survivorCode.getText());
        searchValues.put(AGE_FROM, ageFrom.getText().isEmpty() ? String.valueOf(EMPTY_AGE) : ageFrom.getText());
        searchValues.put(AGE_TO, ageTo.getText().isEmpty() ? String.valueOf(EMPTY_AGE) : ageTo.getText());
        searchValues.put(TYPE_OF_VIOLENCE, getValueOfSelectedView(typeOfViolence));
        searchValues.put(LOCATION, getValueOfSelectedView(location));

        return searchValues;
    }

    @Override
    protected void onInitSearchFields() {
        idField.setVisibility(View.VISIBLE);
        survivorCodeField.setVisibility(View.VISIBLE);
        ageField.setVisibility(View.VISIBLE);
        typeOfViolenceField.setVisibility(View.VISIBLE);
        locationField.setVisibility(View.VISIBLE);

        initTypeOfViolenceField();
        initIncidentLocationField();
    }

    @Override
    protected RecordListAdapter createRecordListAdapter() {
        return incidentListAdapter;
    }

    private void initIncidentLocationField() {
        final List<String> locationVals = incidentSearchPresenter.getIncidentLocationList();
        locationVals.add(0, "");
        final StringSpinnerAdapter adapter = generateTypeOfViolenceListAdapter(locationVals, "Incident Location");
        location.setAdapter(adapter);

        location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                adapter.setValue(view, locationVals.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void initTypeOfViolenceField() {
        final List<String> typeOfViolenceVals = incidentSearchPresenter.getViolenceTypeList();
        typeOfViolenceVals.add(0, "");
        final StringSpinnerAdapter adapter = generateTypeOfViolenceListAdapter(typeOfViolenceVals, "Type of Violence");
        typeOfViolence.setAdapter(adapter);

        typeOfViolence.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                adapter.setValue(view, typeOfViolenceVals.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private StringSpinnerAdapter generateTypeOfViolenceListAdapter(List<String> violenceTypeList, String hintVal) {
        StringSpinnerAdapter adapter = new StringSpinnerAdapter(getActivity(), R.layout.string_list_spinner_closed, violenceTypeList, hintVal);
        return adapter;
    }

    private String getValueOfSelectedView(Spinner spinner) {
        LinearLayout selectedView = (LinearLayout) spinner.getSelectedView();
        ClearableEditText textField = (ClearableEditText) selectedView.findViewById(R.id.string_value_closed);
        return textField.getText();
    }

}
