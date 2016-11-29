package org.unicef.rapidreg.childcase;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.Feature;
import org.unicef.rapidreg.base.RecordActivity;
import org.unicef.rapidreg.base.RecordPhotoAdapter;
import org.unicef.rapidreg.base.RecordRegisterAdapter;
import org.unicef.rapidreg.base.RecordRegisterFragment;
import org.unicef.rapidreg.base.RecordRegisterPresenter;
import org.unicef.rapidreg.event.SaveCaseEvent;
import org.unicef.rapidreg.forms.CaseFormRoot;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.forms.RecordForm;
import org.unicef.rapidreg.forms.Section;
import org.unicef.rapidreg.injection.component.DaggerFragmentComponent;
import org.unicef.rapidreg.injection.module.FragmentModule;
import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.CasePhotoService;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.cache.ItemValues;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.utils.JsonUtils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.OnClick;

public class CaseMiniFormFragment extends RecordRegisterFragment {
    public static final String TAG = CaseMiniFormFragment.class.getSimpleName();

    @Inject
    CaseRegisterPresenter caseRegisterPresenter;

    @NonNull
    @Override
    public RecordRegisterPresenter createPresenter() {
        return caseRegisterPresenter;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void saveCase(SaveCaseEvent event) {
        if (!validateRequiredField()) {
            Toast.makeText(getActivity(), R.string.required_field_is_not_filled, Toast.LENGTH_LONG).show();
            return;
        }

        clearProfileItems();

        ArrayList<String> photoPaths = (ArrayList<String>) photoAdapter.getAllItems();
        ItemValues itemValues = new ItemValues(new Gson().fromJson(new Gson().toJson(
                this.itemValues.getValues()), JsonObject.class));

        try {
            Case record = caseRegisterPresenter.saveCase(itemValues, photoPaths);
            Toast.makeText(getActivity(), R.string.save_success, Toast.LENGTH_SHORT).show();

            Bundle args = new Bundle();
            args.putLong(CaseService.CASE_PRIMARY_ID, record.getId());
            ((RecordActivity) getActivity()).turnToFeature(CaseFeature.DETAILS_MINI, args, null);
        } catch (IOException e) {
            Toast.makeText(getActivity(), R.string.save_failed, Toast.LENGTH_SHORT).show();
        }
        Case record = CaseService.getInstance().getByUniqueId(itemValues.getAsString(CaseService.CASE_ID));

        Bundle args = new Bundle();
        args.putLong(CaseService.CASE_PRIMARY_ID, record.getId());
        ((RecordActivity) getActivity()).turnToFeature(CaseFeature.DETAILS_MINI, args, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getComponent().inject(this);
        super.onCreateView(inflater, container, savedInstanceState);
        initItemValues();

        return inflater.inflate(R.layout.fragment_register, container, false);
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        List<Field> fields = getFields();
        presenter.initContext(getActivity(), fields, true);
    }

    @Override
    protected List<Field> getFields() {
        List<Field> fields = new ArrayList<>();

        RecordForm form = caseRegisterPresenter.getCurrentForm();

        List<Section> sections = form.getSections();

        for (Section section : sections) {
            for (Field field : section.getFields()) {
                if (field.isShowOnMiniForm()) {
                    if (field.isPhotoUploadBox()) {
                        fields.add(0, field);
                    } else {
                        fields.add(field);
                    }
                }
            }
        }
        if (!fields.isEmpty()) {
            addProfileFieldForDetailsPage(fields);
        }

        return fields;
    }

    @Override
    public void initView(RecordRegisterAdapter adapter) {
        super.initView(adapter);
        formSwitcher.setText(R.string.show_more_details);

        if (((RecordActivity) getActivity()).getCurrentFeature().isDetailMode()) {
            editButton.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.edit)
    public void onEditClicked() {
        Bundle args = new Bundle();
        args.putSerializable(RecordService.ITEM_VALUES, itemValues);
        args.putStringArrayList(RecordService.RECORD_PHOTOS, (ArrayList<String>) photoAdapter.getAllItems());
        ((CaseActivity) getActivity()).turnToFeature(CaseFeature.EDIT_MINI, args, null);
    }

    @OnClick(R.id.form_switcher)
    public void onSwitcherChecked() {
        Bundle args = new Bundle();
        args.putSerializable(RecordService.ITEM_VALUES, itemValues);
        args.putStringArrayList(RecordService.RECORD_PHOTOS, (ArrayList<String>) photoAdapter.getAllItems());
        Feature feature = ((RecordActivity) getActivity()).getCurrentFeature().isDetailMode() ?
                CaseFeature.DETAILS_FULL : ((RecordActivity) getActivity()).getCurrentFeature().isAddMode() ?
                CaseFeature.ADD_FULL : CaseFeature.EDIT_FULL;
        ((RecordActivity) getActivity()).turnToFeature(feature, args, ANIM_TO_FULL);
    }

    protected void initItemValues() {
        if (getArguments() != null) {
            recordId = getArguments().getLong(CaseService.CASE_PRIMARY_ID, INVALID_RECORD_ID);
            if (recordId != INVALID_RECORD_ID) {
                Case item = CaseService.getInstance().getById(recordId);
                String caseJson = new String(item.getContent().getBlob());
                try {
                    itemValues = new ItemValuesMap(JsonUtils.toMap(ItemValues.generateItemValues(caseJson).getValues()));
                    itemValues.addStringItem(CaseService.CASE_ID, item.getUniqueId());
                } catch (JSONException e) {
                    Log.e(TAG, "Json conversion error");
                }

                DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
                String shortUUID = RecordService.getShortUUID(item.getUniqueId());
                itemValues.addStringItem(ItemValues.RecordProfile.ID_NORMAL_STATE, shortUUID);
                itemValues.addStringItem(ItemValues.RecordProfile.REGISTRATION_DATE,
                        dateFormat.format(item.getRegistrationDate()));
                itemValues.addNumberItem(ItemValues.RecordProfile.ID, item.getId());
                photoAdapter = initPhotoAdapter(recordId);
            } else {
                itemValues = (ItemValuesMap) getArguments().getSerializable(ITEM_VALUES);
                photoAdapter = new CasePhotoAdapter(getContext(),
                        getArguments().getStringArrayList(RecordService.RECORD_PHOTOS));
            }
        } else {
            itemValues = new ItemValuesMap();
            photoAdapter = new CasePhotoAdapter(getContext(), new ArrayList<String>());
        }
    }


    private RecordPhotoAdapter initPhotoAdapter(long recordId) {
        List<String> paths = new ArrayList<>();

        List<Long> casePhotoIds = CasePhotoService.getInstance().getIdsByCaseId(recordId);

        for (Long casePhotoId : casePhotoIds) {
            paths.add(String.valueOf(casePhotoId));
        }
        return new CasePhotoAdapter(getContext(), paths);
    }

    private boolean validateRequiredField() {
        CaseFormRoot caseForm = caseRegisterPresenter.getCurrentForm();
        return RecordService.validateRequiredFields(caseForm, itemValues);
    }
}
