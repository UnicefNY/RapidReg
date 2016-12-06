package org.unicef.rapidreg.childcase.caseregister;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.Feature;
import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.base.record.recordphoto.RecordPhotoAdapter;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterAdapter;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterFragment;
import org.unicef.rapidreg.childcase.CaseActivity;
import org.unicef.rapidreg.childcase.CaseFeature;
import org.unicef.rapidreg.childcase.casephoto.CasePhotoAdapter;
import org.unicef.rapidreg.event.SaveCaseEvent;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.cache.ItemValuesMap;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.OnClick;

public class CaseMiniFormFragment extends RecordRegisterFragment {
    public static final String TAG = CaseMiniFormFragment.class.getSimpleName();

    @Inject
    CaseRegisterPresenter caseRegisterPresenter;

    @Inject
    CasePhotoAdapter casePhotoAdapter;

    @NonNull
    @Override
    public CaseRegisterPresenter createPresenter() {
        return caseRegisterPresenter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getComponent().inject(this);
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onInitViewContent() {
        super.onInitViewContent();
        addProfileFieldForDetailsPage(caseRegisterPresenter.getFields());
        formSwitcher.setText(R.string.show_more_details);
        if (((RecordActivity) getActivity()).getCurrentFeature().isDetailMode()) {
            editButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected RecordRegisterAdapter createRecordRegisterAdapter() {
        RecordRegisterAdapter recordRegisterAdapter = new RecordRegisterAdapter(getActivity(),
                caseRegisterPresenter.getValidFields(),
                caseRegisterPresenter.getDefaultItemValues(),
                true);

        casePhotoAdapter.setItems(caseRegisterPresenter.getDefaultPhotoPaths());
        recordRegisterAdapter.setPhotoAdapter(casePhotoAdapter);

        return recordRegisterAdapter;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void saveCase(SaveCaseEvent event) {
        caseRegisterPresenter.saveRecord(this);
    }

    @Override
    public void onSaveSuccessful(long recordId) {
        Toast.makeText(getActivity(), R.string.save_success, Toast.LENGTH_SHORT).show();
        Bundle args = new Bundle();
        args.putLong(CaseService.CASE_PRIMARY_ID, recordId);
        ((RecordActivity)getActivity()).turnToFeature(CaseFeature.DETAILS_MINI, args, null);
    }

    @OnClick(R.id.edit)
    public void onEditClicked() {
        Bundle args = new Bundle();
        args.putSerializable(RecordService.ITEM_VALUES, getRecordRegisterData());
        args.putStringArrayList(RecordService.RECORD_PHOTOS, (ArrayList<String>) getPhotoPathsData());
        ((CaseActivity) getActivity()).turnToFeature(CaseFeature.EDIT_MINI, args, null);
    }

    @OnClick(R.id.form_switcher)
    public void onSwitcherChecked() {
        Bundle args = new Bundle();
        args.putSerializable(RecordService.ITEM_VALUES, getRecordRegisterData());
        args.putStringArrayList(RecordService.RECORD_PHOTOS, (ArrayList<String>) getPhotoPathsData());
        Feature feature = ((RecordActivity) getActivity()).getCurrentFeature().isDetailMode() ?
                CaseFeature.DETAILS_FULL : ((RecordActivity) getActivity()).getCurrentFeature().isAddMode() ?
                CaseFeature.ADD_FULL : CaseFeature.EDIT_FULL;
        ((RecordActivity) getActivity()).turnToFeature(feature, args, ANIM_TO_FULL);
    }
}
