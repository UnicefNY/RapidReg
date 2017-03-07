package org.unicef.rapidreg.incident.incidentregister;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.base.record.recordphoto.RecordPhotoAdapter;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterWrapperFragment;
import org.unicef.rapidreg.event.SaveIncidentEvent;
import org.unicef.rapidreg.forms.Section;
import org.unicef.rapidreg.incident.IncidentActivity;
import org.unicef.rapidreg.incident.IncidentFeature;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.cache.ItemValuesMap;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.OnClick;

import static org.unicef.rapidreg.IntentSender.BUNDLE_EXTRA;
import static org.unicef.rapidreg.service.CaseService.CASE_ID;

public class IncidentRegisterWrapperFragment extends RecordRegisterWrapperFragment {

    public static final String TAG = IncidentRegisterWrapperFragment.class.getSimpleName();

    @Inject
    IncidentRegisterPresenter incidentRegisterPresenter;

    @Override
    public IncidentRegisterPresenter createPresenter() {
        return incidentRegisterPresenter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        getComponent().inject(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected RecordPhotoAdapter createRecordPhotoAdapter() {
        return null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void saveIncident(SaveIncidentEvent event) {
        ItemValuesMap recordRegisterData = getRecordRegisterData();
        String caseId = getArguments().getString(CASE_ID);
        if (caseId != null) {
            recordRegisterData.addStringItem(CASE_ID, caseId);
        }
        incidentRegisterPresenter.saveRecord(recordRegisterData, getPhotoPathsData(), this);
    }

    @OnClick(R.id.edit)
    public void onEditClicked() {
        Bundle args = new Bundle();
        args.putSerializable(RecordService.ITEM_VALUES, getRecordRegisterData());
        ((IncidentActivity) getActivity()).turnToFeature(IncidentFeature.EDIT_FULL, args, null);
    }

    @Override
    protected void initItemValues() {
        if (getArguments() != null) {
            setRecordRegisterData((ItemValuesMap) getArguments().getSerializable(RecordService.ITEM_VALUES));
            setFieldValueVerifyResult((ItemValuesMap) getArguments().getSerializable(RecordService.VERIFY_MESSAGE));
        }
    }

    @Override
    protected void initFormData() {
        form = incidentRegisterPresenter.getTemplateForm();
        sections = form.getSections();
    }

    @NonNull
    protected FragmentPagerItems getPages() {
        FragmentPagerItems pages = new FragmentPagerItems(getActivity());
        for (Section section : sections) {
            String[] values = section.getName().values().toArray(new String[0]);
            Bundle args = new Bundle();
            args.putSerializable(RecordService.ITEM_VALUES, getRecordRegisterData());
            args.putSerializable(RecordService.VERIFY_MESSAGE, getFieldValueVerifyResult());
            pages.add(FragmentPagerItem.of(values[0], IncidentRegisterFragment.class, args));
        }
        return pages;
    }

    @Override
    public void onSaveSuccessful(long recordId) {
        Bundle args = new Bundle();
        args.putSerializable(RecordService.ITEM_VALUES, getRecordRegisterData());
        ((RecordActivity) getActivity()).turnToFeature(IncidentFeature.DETAILS_FULL, args, null);
    }

}
