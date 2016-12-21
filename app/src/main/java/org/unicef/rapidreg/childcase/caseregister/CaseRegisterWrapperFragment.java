package org.unicef.rapidreg.childcase.caseregister;

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
import org.unicef.rapidreg.base.Feature;
import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.base.record.recordphoto.RecordPhotoAdapter;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterWrapperFragment;
import org.unicef.rapidreg.childcase.CaseActivity;
import org.unicef.rapidreg.childcase.casephoto.CasePhotoAdapter;
import org.unicef.rapidreg.event.SaveCaseEvent;
import org.unicef.rapidreg.forms.Section;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.OnClick;

import static org.unicef.rapidreg.childcase.CaseFeature.*;
import static org.unicef.rapidreg.childcase.caseregister.CaseRegisterPresenter.MODULE_CASE_CP;
import static org.unicef.rapidreg.service.RecordService.MODULE;

public class CaseRegisterWrapperFragment extends RecordRegisterWrapperFragment {
    public static final String TAG = CaseRegisterWrapperFragment.class.getSimpleName();

    @Inject
    CaseRegisterPresenter caseRegisterPresenter;

    @Inject
    CasePhotoAdapter casePhotoAdapter;

    @Override
    public CaseRegisterPresenter createPresenter() {
        if (getArguments() != null && getArguments().containsKey(MODULE)) {
            caseRegisterPresenter.setCaseType(getArguments().getString(MODULE));
        }
        return caseRegisterPresenter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getComponent().inject(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected RecordPhotoAdapter createRecordPhotoAdapter() {
        casePhotoAdapter.setItems(getArguments().getStringArrayList(RecordService.RECORD_PHOTOS));
        return casePhotoAdapter;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void saveCase(SaveCaseEvent event) {
       caseRegisterPresenter.saveRecord(getRecordRegisterData(), getPhotoPathsData(), this);
    }

    @OnClick(R.id.edit)
    public void onEditClicked() {
        Bundle args = new Bundle();
        args.putString(MODULE, caseRegisterPresenter.getCaseType());
        args.putSerializable(RecordService.ITEM_VALUES, getRecordRegisterData());
        args.putStringArrayList(RecordService.RECORD_PHOTOS, (ArrayList<String>) recordPhotoAdapter.getAllItems());

        ((CaseActivity) getActivity()).turnToFeature(EDIT_FULL, args, null);
    }

    @Override
    protected void initItemValues() {
        if (getArguments() != null) {
            setRecordRegisterData((ItemValuesMap) getArguments().getSerializable(ITEM_VALUES));
        }
    }

    @Override
    protected void initFormData() {
        form = caseRegisterPresenter.getTemplateForm();
        sections = form.getSections();
    }

    @NonNull
    protected FragmentPagerItems getPages() {
        FragmentPagerItems pages = new FragmentPagerItems(getActivity());
        for (Section section : sections) {
            String[] values = section.getName().values().toArray(new String[0]);
            Bundle args = new Bundle();
            args.putString(MODULE, caseRegisterPresenter.getCaseType());
            args.putSerializable(RecordService.ITEM_VALUES, getRecordRegisterData());
            args.putStringArrayList(RecordService.RECORD_PHOTOS, (ArrayList<String>) recordPhotoAdapter.getAllItems());
            pages.add(FragmentPagerItem.of(values[0], CaseRegisterFragment.class, args));
        }
        return pages;
    }

    @Override
    public void onSaveSuccessful(long recordId) {
        String moduleId = caseRegisterPresenter.getCaseType();
        Feature feature = moduleId.equals(MODULE_CASE_CP) ? DETAILS_CP_FULL : DETAILS_GBV_FULL;

        Bundle args = new Bundle();
        args.putLong(CaseService.CASE_PRIMARY_ID, recordId);
        args.putString(MODULE, caseRegisterPresenter.getCaseType());
        args.putSerializable(RecordService.ITEM_VALUES, getRecordRegisterData());
        args.putStringArrayList(RecordService.RECORD_PHOTOS, (ArrayList<String>) getPhotoPathsData());

        ((RecordActivity) getActivity()).turnToFeature(feature, args, null);
    }
}
