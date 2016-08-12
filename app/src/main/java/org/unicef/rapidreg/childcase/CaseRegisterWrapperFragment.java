package org.unicef.rapidreg.childcase;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.RecordActivity;
import org.unicef.rapidreg.base.RecordRegisterWrapperFragment;
import org.unicef.rapidreg.event.SaveCaseEvent;
import org.unicef.rapidreg.forms.CaseFormRoot;
import org.unicef.rapidreg.forms.Section;
import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.cache.ItemValues;
import org.unicef.rapidreg.service.cache.ItemValuesMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;

public class CaseRegisterWrapperFragment extends RecordRegisterWrapperFragment {
    public static final String TAG = CaseRegisterWrapperFragment.class.getSimpleName();

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void saveCase(SaveCaseEvent event) {
        if (!validateRequiredField()) {
            Toast.makeText(getActivity(), R.string.required_field_is_not_filled, Toast.LENGTH_LONG).show();
            return;
        }

        clearProfileItems();

        ArrayList<String> photoPaths = (ArrayList<String>) recordPhotoAdapter.getAllItems();
        ItemValues itemValues = new ItemValues(new Gson().fromJson(new Gson().toJson(
                this.itemValues.getValues()), JsonObject.class));

        try {
            saveCase(itemValues, photoPaths);
            Toast.makeText(getActivity(), R.string.save_success, Toast.LENGTH_SHORT).show();
            Bundle args = new Bundle();
            args.putSerializable(RecordService.ITEM_VALUES, ItemValuesMap.fromItemValuesJsonObject(itemValues));
            args.putStringArrayList(RecordService.RECORD_PHOTOS, photoPaths);
            ((RecordActivity) getActivity()).turnToFeature(CaseFeature.DETAILS_FULL, args, null);
        } catch (IOException e) {
            Toast.makeText(getActivity(), R.string.save_failed, Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.edit)
    public void onEditClicked() {
        Bundle args = new Bundle();
        args.putSerializable(RecordService.ITEM_VALUES, itemValues);
        args.putStringArrayList(RecordService.RECORD_PHOTOS, (ArrayList<String>) recordPhotoAdapter.getAllItems());
        ((CaseActivity) getActivity()).turnToFeature(CaseFeature.EDIT_FULL, args, null);
    }

    @Override
    protected void initItemValues() {
        if (getArguments() != null) {
            itemValues = (ItemValuesMap) getArguments().getSerializable(ITEM_VALUES);
            recordPhotoAdapter = new CasePhotoAdapter(getContext(),
                    getArguments().getStringArrayList(RecordService.RECORD_PHOTOS));
        }
    }

    @Override
    protected void initFormData() {
        form = CaseFormService.getInstance().getCurrentForm();
        sections = form.getSections();
    }

    @NonNull
    protected FragmentPagerItems getPages() {
        FragmentPagerItems pages = new FragmentPagerItems(getActivity());
        for (Section section : sections) {
            String[] values = section.getName().values().toArray(new String[0]);
            Bundle args = new Bundle();
            args.putSerializable(RecordService.ITEM_VALUES, itemValues);
            args.putStringArrayList(RecordService.RECORD_PHOTOS, (ArrayList<String>) recordPhotoAdapter.getAllItems());
            pages.add(FragmentPagerItem.of(values[0], CaseRegisterFragment.class, args));
        }
        return pages;
    }

    private boolean validateRequiredField() {
        CaseFormRoot caseForm = CaseFormService.getInstance().getCurrentForm();
        return RecordService.validateRequiredFields(caseForm, itemValues);
    }

    private Case saveCase(ItemValues itemValues, List<String> photoPaths) throws IOException {
        return CaseService.getInstance().saveOrUpdate(itemValues, photoPaths);
    }
}
