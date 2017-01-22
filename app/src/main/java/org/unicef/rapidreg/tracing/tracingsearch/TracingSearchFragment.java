package org.unicef.rapidreg.tracing.tracingsearch;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.record.recordlist.RecordListAdapter;
import org.unicef.rapidreg.base.record.recordsearch.RecordSearchFragment;
import org.unicef.rapidreg.base.record.recordsearch.RecordSearchPresenter;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.tracing.tracinglist.TracingListAdapter;
import org.unicef.rapidreg.utils.StreamUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import static org.unicef.rapidreg.base.record.recordsearch.RecordSearchPresenter.*;
import static org.unicef.rapidreg.base.record.recordsearch.RecordSearchPresenter.CONSTANT.AGE_FROM;
import static org.unicef.rapidreg.base.record.recordsearch.RecordSearchPresenter.CONSTANT.AGE_TO;
import static org.unicef.rapidreg.model.RecordModel.EMPTY_AGE;

public class TracingSearchFragment extends RecordSearchFragment {

    @Inject
    TracingSearchPresenter tracingSearchPresenter;

    @Inject
    TracingListAdapter tracingListAdapter;

    @Override
    @NonNull
    public RecordSearchPresenter createPresenter() {
        return tracingSearchPresenter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getComponent().inject(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onInitViewContent() {
        super.onInitViewContent();
        caregiver.setVisibility(View.GONE);
        caregiverSeparator.setVisibility(View.GONE);

        registrationDate.setHint(R.string.inquiry_date);
    }

    @Override
    protected Map<String, String> getFilterValues() {
        Map<String, String> searchValues = new LinkedHashMap<>();
        searchValues.put(CONSTANT.ID, id.getText());
        searchValues.put(CONSTANT.NAME, name.getText());
        searchValues.put(CONSTANT.DATE_OF_INQUIRY, dateOfInquiry.getText().toString());
        searchValues.put(AGE_FROM, ageFrom.getText().isEmpty() ? String.valueOf(EMPTY_AGE) : ageFrom.getText());
        searchValues.put(AGE_TO, ageTo.getText().isEmpty() ? String.valueOf(EMPTY_AGE) : ageTo.getText());

        return searchValues;
    }

    @Override
    protected void onInitSearchFields() {
        idField.setVisibility(View.VISIBLE);
        nameField.setVisibility(View.VISIBLE);
        ageField.setVisibility(View.VISIBLE);
        dateOfInquiryField.setVisibility(View.VISIBLE);
    }

    @Override
    protected RecordListAdapter createRecordListAdapter() {
        return tracingListAdapter;
    }
}
