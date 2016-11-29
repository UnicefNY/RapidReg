package org.unicef.rapidreg.childcase;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.CaseListPresenter;
import org.unicef.rapidreg.base.RecordActivity;
import org.unicef.rapidreg.base.RecordListAdapter;
import org.unicef.rapidreg.base.RecordListFragment;
import org.unicef.rapidreg.base.RecordListPresenter;
import org.unicef.rapidreg.model.Case;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.OnClick;


public class CaseListFragment extends RecordListFragment {

    private static final SpinnerState[] SPINNER_STATES = {
            SpinnerState.AGE_ASC,
            SpinnerState.AGE_DES,
            SpinnerState.REG_DATE_ASC,
            SpinnerState.REG_DATE_DES};

    private static final int DEFAULT_SPINNER_STATE_POSITION =
            Arrays.asList(SPINNER_STATES).indexOf(SpinnerState.REG_DATE_DES);

    @Inject
    CaseListPresenter caseListPresenter;

    @Override
    public RecordListPresenter createPresenter() {
        return caseListPresenter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getComponent().inject(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void initListContainer(final RecordListAdapter adapter) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listContainer.setLayoutManager(layoutManager);
        listContainer.setAdapter(adapter);

        List<Case> cases = caseListPresenter.getCases();
        int index = cases.isEmpty() ? HAVE_NO_RESULT : HAVE_RESULT_LIST;
        viewSwitcher.setDisplayedChild(index);
    }

    @Override
    protected void initOrderSpinner(final RecordListAdapter adapter) {
        orderSpinner.setAdapter(new SpinnerAdapter(getActivity(),
                R.layout.record_list_spinner_opened, Arrays.asList(SPINNER_STATES)));
        orderSpinner.setSelection(DEFAULT_SPINNER_STATE_POSITION);
        orderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<Long> filterCases = caseListPresenter.getCasesByFilter(SPINNER_STATES[position]);
                if (filterCases == null || filterCases.isEmpty()) {
                    return;
                }
                adapter.setRecordList(filterCases);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @OnClick(R.id.add)
    public void onCaseAddClicked() {
        presenter.clearAudioFile();
        RecordActivity activity = (RecordActivity) getActivity();

        if (!caseListPresenter.isFormReady()) {
            showSyncFormDialog(getResources().getString(R.string.child_case));
            return;
        }

        activity.turnToFeature(CaseFeature.ADD_MINI, null, null);
    }

}
