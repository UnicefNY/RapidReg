package org.unicef.rapidreg.incident.incidentsearch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.record.recordlist.RecordListAdapter;
import org.unicef.rapidreg.base.record.recordsearch.RecordSearchFragment;
import org.unicef.rapidreg.base.record.recordsearch.RecordSearchPresenter;
import org.unicef.rapidreg.base.record.recordsearch.StringSpinnerAdapter;
import org.unicef.rapidreg.incident.incidentlist.IncidentListAdapter;
import org.unicef.rapidreg.utils.StreamUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import static org.unicef.rapidreg.base.record.recordsearch.RecordSearchPresenter.AGE_FROM;
import static org.unicef.rapidreg.base.record.recordsearch.RecordSearchPresenter.AGE_TO;
import static org.unicef.rapidreg.base.record.recordsearch.RecordSearchPresenter.ID;
import static org.unicef.rapidreg.base.record.recordsearch.RecordSearchPresenter.LOCATION;
import static org.unicef.rapidreg.base.record.recordsearch.RecordSearchPresenter.NAME;
import static org.unicef.rapidreg.base.record.recordsearch.RecordSearchPresenter.SURVIVOR_CODE;
import static org.unicef.rapidreg.base.record.recordsearch.RecordSearchPresenter.TYPE_OF_VIOLENCE;
import static org.unicef.rapidreg.model.RecordModel.EMPTY_AGE;

public class IncidentSearchFragment extends RecordSearchFragment {

    @Inject
    IncidentSearchPresenter incidentSearchPresenter;

    @Inject
    IncidentListAdapter incidentListAdapter;

    private String typeOfViolenceVal = "";

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
        searchValues.put(TYPE_OF_VIOLENCE, typeOfViolenceVal);
        searchValues.put(LOCATION, location.getText());

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
