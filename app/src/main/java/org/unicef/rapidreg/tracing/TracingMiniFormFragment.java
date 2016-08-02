package org.unicef.rapidreg.tracing;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.Feature;
import org.unicef.rapidreg.base.RecordActivity;
import org.unicef.rapidreg.base.RecordPhotoAdapter;
import org.unicef.rapidreg.base.RecordRegisterAdapter;
import org.unicef.rapidreg.base.RecordRegisterFragment;
import org.unicef.rapidreg.event.SaveTracingEvent;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.forms.RecordForm;
import org.unicef.rapidreg.forms.Section;
import org.unicef.rapidreg.forms.TracingFormRoot;
import org.unicef.rapidreg.model.Tracing;
import org.unicef.rapidreg.model.TracingPhoto;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.TracingFormService;
import org.unicef.rapidreg.service.TracingPhotoService;
import org.unicef.rapidreg.service.TracingService;
import org.unicef.rapidreg.service.cache.ItemValues;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.utils.JsonUtils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.OnClick;

public class TracingMiniFormFragment extends RecordRegisterFragment {
    public static final String TAG = TracingMiniFormFragment.class.getSimpleName();

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void saveTracing(SaveTracingEvent event) {
        if (validateRequiredField()) {
            clearProfileItems();

            ArrayList<String> photoPaths = (ArrayList<String>) photoAdapter.getAllItems();
            ItemValues itemValues = new ItemValues(new Gson().fromJson(new Gson().toJson(
                    this.itemValues.getValues()), JsonObject.class));

            try {
                Tracing record = saveTracing(itemValues, photoPaths);
                Toast.makeText(getActivity(), R.string.save_success, Toast.LENGTH_SHORT).show();

                Bundle args = new Bundle();
                args.putLong(TracingService.TRACING_PRIMARY_ID, record.getId());
                ((RecordActivity) getActivity()).turnToFeature(TracingFeature.DETAILS_MINI, args, null);
            } catch (IOException e) {
                Toast.makeText(getActivity(), R.string.save_failed, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
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

        RecordForm form = TracingFormService.getInstance().getCurrentForm();
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
        ((TracingActivity) getActivity()).turnToFeature(TracingFeature.EDIT_MINI, args, null);
    }

    @OnClick(R.id.form_switcher)
    public void onSwitcherChecked() {
        Bundle args = new Bundle();
        args.putSerializable(RecordService.ITEM_VALUES, itemValues);
        args.putStringArrayList(RecordService.RECORD_PHOTOS, (ArrayList<String>) photoAdapter.getAllItems());
        Feature feature = ((RecordActivity) getActivity()).getCurrentFeature().isDetailMode() ?
                TracingFeature.DETAILS_FULL : ((RecordActivity) getActivity()).getCurrentFeature().isAddMode() ?
                TracingFeature.ADD_FULL : TracingFeature.EDIT_FULL;
        ((RecordActivity) getActivity()).turnToFeature(feature, args, ANIM_TO_FULL);
    }

    protected void initItemValues() {
        if (getArguments() != null) {
            recordId = getArguments().getLong(TracingService.TRACING_PRIMARY_ID, INVALID_RECORD_ID);
            if (recordId != INVALID_RECORD_ID) {
                Tracing item = TracingService.getInstance().getById(recordId);
                String tracingJson = new String(item.getContent().getBlob());
                try {
                    itemValues = new ItemValuesMap(JsonUtils.toMap(ItemValues.generateItemValues(tracingJson).getValues()));
                    itemValues.addStringItem(TracingService.TRACING_ID, item.getUniqueId());
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
                photoAdapter = new TracingPhotoAdapter(getContext(),
                        getArguments().getStringArrayList(RecordService.RECORD_PHOTOS));
            }
        } else {
            itemValues = new ItemValuesMap();
            photoAdapter = new TracingPhotoAdapter(getContext(), new ArrayList<String>());
        }
    }

    private RecordPhotoAdapter initPhotoAdapter(long recordId) {
        List<String> paths = new ArrayList<>();

        List<TracingPhoto> tracings = TracingPhotoService.getInstance().getByTracingId(recordId);
        for (TracingPhoto tracing : tracings) {
            paths.add(String.valueOf(tracing.getId()));
        }
        return new TracingPhotoAdapter(getContext(), paths);
    }

    private boolean validateRequiredField() {
        TracingFormRoot tracingForm = TracingFormService.getInstance().getCurrentForm();
        List<String> requiredFieldNames = new ArrayList<>();

        for (Section section : tracingForm.getSections()) {
            Collections.addAll(requiredFieldNames, RecordService
                    .fetchRequiredFiledNames(section.getFields()).toArray(new String[0]));
        }
        for (String field : requiredFieldNames) {
            if (TextUtils.isEmpty((CharSequence) itemValues.getValues().get(field))) {
                Toast.makeText(getActivity(), R.string.required_field_is_not_filled, Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    private Tracing saveTracing(ItemValues itemValues, List<String> photoPaths) throws IOException {
        return TracingService.getInstance().saveOrUpdate(itemValues, photoPaths);
    }
}
