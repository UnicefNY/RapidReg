package org.unicef.rapidreg.childcase;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.AdapterView;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.RecordListAdapter;
import org.unicef.rapidreg.base.RecordListFragment;
import org.unicef.rapidreg.base.RecordListPresenter;
import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.service.RecordService;

import java.util.Arrays;
import java.util.List;

import butterknife.OnClick;


public class CaseListFragment extends RecordListFragment {

    private static final SpinnerState[] SPINNER_STATES = {
            SpinnerState.AGE_ASC,
            SpinnerState.AGE_DES,
            SpinnerState.REG_DATE_ASC,
            SpinnerState.REG_DATE_DES};

    private static final int DEFAULT_SPINNER_STATE_POSITION =
            Arrays.asList(SPINNER_STATES).indexOf(SpinnerState.REG_DATE_DES);

    @Override
    public RecordListPresenter createPresenter() {
        return new RecordListPresenter(RecordModel.CASE);
    }

    @Override
    protected void initListContainer(final RecordListAdapter adapter) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listContainer.setLayoutManager(layoutManager);
        listContainer.setAdapter(adapter);

        List<Case> cases = CaseService.getInstance().getAll();
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
                handleItemSelection(position);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

            private void handleItemSelection(int position) {
                CaseService service = CaseService.getInstance();
                switch (SPINNER_STATES[position]) {
                    case AGE_ASC:
                        adapter.setRecordList(service.getAllOrderByAgeASC());
                        break;
                    case AGE_DES:
                        adapter.setRecordList(service.getAllOrderByAgeDES());
                        break;
                    case REG_DATE_ASC:
                        adapter.setRecordList(service.getAllOrderByDateASC());
                        break;
                    case REG_DATE_DES:
                        adapter.setRecordList(service.getAllOrderByDateDES());
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @OnClick(R.id.add)
    public void onCaseAddClicked() {
        RecordService.clearAudioFile();

        if (!CaseFormService.getInstance().isFormReady()) {
            showSyncFormDialog(getResources().getString(R.string.child_case));
            return;
        }

        CaseActivity activity = (CaseActivity) getActivity();
        activity.turnToFeature(CaseFeature.ADD_MINI, null, null);
    }
}
