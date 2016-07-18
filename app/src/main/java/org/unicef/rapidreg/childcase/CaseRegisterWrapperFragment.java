package org.unicef.rapidreg.childcase;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.RecordPhotoAdapter;
import org.unicef.rapidreg.base.RecordRegisterWrapperFragment;
import org.unicef.rapidreg.event.SaveCaseEvent;
import org.unicef.rapidreg.forms.CaseFormRoot;
import org.unicef.rapidreg.forms.Section;
import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.model.CasePhoto;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.CasePhotoService;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.cache.ItemValues;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.utils.JsonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.OnClick;

public class CaseRegisterWrapperFragment extends RecordRegisterWrapperFragment {

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void saveCase(SaveCaseEvent event) {
        if (validateRequiredField()) {
            List<String> photoPaths = recordPhotoAdapter.getAllItems();
            ItemValues itemValues = new ItemValues(new Gson().fromJson(new Gson().toJson(this.itemValues.getValues()), JsonObject.class));
            boolean saveStatus = saveAndGetSucceedStatus(itemValues,photoPaths);
            if ( saveStatus == true ) {
                Toast.makeText(getActivity(), R.string.save_success,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), R.string.save_failed,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @OnClick(R.id.edit)
    public void onEditClicked() {
        Bundle args = new Bundle();
        args.putLong(CaseService.CASE_ID, recordId);
        ((CaseActivity) getActivity()).turnToDetailOrEditPage(CaseFeature.EDIT, args);
    }

    @Override
    protected void initItemValues() {
        if (getArguments() != null) {
            recordId = getArguments().getLong(CaseService.CASE_ID);
            Case caseItem = CaseService.getInstance().getById(recordId);
            String caseJson = new String(caseItem.getContent().getBlob());
            try {
                itemValues = new ItemValuesMap(JsonUtils.toMap(ItemValues.generateItemValues(caseJson).getValues()));
                itemValues.addStringItem(CaseService.CASE_ID, caseItem.getUniqueId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            initProfile(caseItem);
        }
    }

    @Override
    protected void initFormData() {
        form = CaseFormService.getInstance().getCurrentForm();
        sections = form.getSections();
        miniFields = new ArrayList<>();
        if (form != null) {
            getMiniFields();
        }
    }

    @Override
    protected RecordPhotoAdapter initPhotoAdapter() {
        recordPhotoAdapter = new CasePhotoAdapter(getContext(), new ArrayList<String>());

        List<CasePhoto> cases = CasePhotoService.getInstance().getByCaseId(recordId);
        for (int i = 0; i < cases.size(); i++) {
            recordPhotoAdapter.addItem(cases.get(i).getId());
        }
        return recordPhotoAdapter;
    }

    @NonNull
    protected FragmentPagerItems getPages() {
        FragmentPagerItems pages = new FragmentPagerItems(getActivity());
        for (Section section : sections) {
            String[] values = section.getName().values().toArray(new String[0]);
            Bundle bundle = new Bundle();
            bundle.putStringArrayList(RecordService.RECORD_PHOTOS,
                    (ArrayList<String>) recordPhotoAdapter.getAllItems());

            bundle.putSerializable(RecordService.ITEM_VALUES, itemValues);
            pages.add(FragmentPagerItem.of(values[0], CaseRegisterFragment.class, bundle));
        }
        return pages;
    }

    private boolean validateRequiredField() {
        CaseFormRoot caseForm = CaseFormService.getInstance().getCurrentForm();
        List<String> requiredFieldNames = new ArrayList<>();

        for (Section section : caseForm.getSections()) {
            Collections.addAll(requiredFieldNames, RecordService
                    .fetchRequiredFiledNames(section.getFields()).toArray(new String[0]));
        }
        for (String field : requiredFieldNames) {
            if (TextUtils.isEmpty((CharSequence) itemValues.getValues().get(field))) {
                Toast.makeText(getActivity(), R.string.required_field_is_not_filled,
                        Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    private boolean saveAndGetSucceedStatus(ItemValues itemValues, List<String> photoPaths) {
        try {
            CaseService.getInstance().saveOrUpdate(itemValues, photoPaths);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
