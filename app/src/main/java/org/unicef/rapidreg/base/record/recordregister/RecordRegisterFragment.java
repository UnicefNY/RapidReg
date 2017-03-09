package org.unicef.rapidreg.base.record.recordregister;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.MvpFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.base.record.recordphoto.RecordPhotoAdapter;
import org.unicef.rapidreg.event.UpdateImageEvent;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.injection.component.DaggerFragmentComponent;
import org.unicef.rapidreg.injection.component.FragmentComponent;
import org.unicef.rapidreg.injection.module.FragmentModule;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import org.unicef.rapidreg.widgets.dialog.MessageDialog;

public abstract class RecordRegisterFragment extends MvpFragment<RecordRegisterView,
        RecordRegisterPresenter>
        implements RecordRegisterView, RecordRegisterView.SaveRecordCallback {

    protected static final int[] ANIM_TO_FULL = {R.anim.slide_in_right, R.anim.slide_out_left};
    protected static final int[] ANIM_TO_MINI = {android.R.anim.slide_in_left, android.R.anim
            .slide_out_right};
    public static final int INVALID_RECORD_ID = -100;
    public static final String INVALID_UNIQUE_ID = "";
    private static final String SUBFORM_STATE = "subform_state";
    private static final String SAVED_STATE_ID = "internalSavedViewState8954201239547";

    @BindView(R.id.register_forms_content)
    RecyclerView fieldList;

    @BindView(R.id.form_switcher)
    protected TextView formSwitcher;

    @BindView(R.id.edit)
    protected FloatingActionButton editButton;

    private RecordRegisterAdapter recordRegisterAdapter;

    protected long recordId;

    Bundle savedState;

    public FragmentComponent getComponent() {
        return DaggerFragmentComponent.builder()
                .applicationComponent(PrimeroApplication.get(getActivity()).getComponent())
                .fragmentModule(new FragmentModule(this))
                .build();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        onInitViewContent();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!restoreStateFromArguments()) {
            onFirstTimeLaunched();
        }
    }

    @Override
    public void onInitViewContent() {
        recordRegisterAdapter = createRecordRegisterAdapter();
        initFieldValueVerifyResult();

        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        manager.setAutoMeasureEnabled(true);

        fieldList.setLayoutManager(manager);

        fieldList.setAdapter(recordRegisterAdapter);
        formSwitcher.setText(R.string.show_short_form);
    }

    private void initFieldValueVerifyResult() {
        Bundle extra = getArguments();
        if (extra != null && extra.containsKey(RecordService.VERIFY_MESSAGE)) {
            setFieldValueVerifyResult((ItemValuesMap) getArguments().getSerializable(RecordService.VERIFY_MESSAGE));
        }
    }

    @Override
    public void setRecordRegisterData(ItemValuesMap itemValues) {
        recordRegisterAdapter.setItemValues(itemValues);
    }

    @Override
    public void setPhotoPathsData(List<String> photoPaths) {
        recordRegisterAdapter.getPhotoAdapter().setItems(photoPaths);
    }

    @Override
    public List<String> getPhotoPathsData() {
        if (recordRegisterAdapter.getPhotoAdapter() == null) {
            return Collections.EMPTY_LIST;
        }
        return recordRegisterAdapter.getPhotoAdapter().getAllItems();
    }

    @Override
    public ItemValuesMap getRecordRegisterData() {
        return recordRegisterAdapter.getItemValues();
    }

    @Override
    public void setFieldValueVerifyResult(ItemValuesMap fieldValueVerifyResult) {
        recordRegisterAdapter.setFieldValueVerifyResult(fieldValueVerifyResult);
    }

    @Override
    public ItemValuesMap getFieldValueVerifyResult() {
        if (recordRegisterAdapter == null) {
            return new ItemValuesMap();
        }
        return recordRegisterAdapter.getFieldValueVerifyResult();
    }

    public void addProfileFieldForDetailsPage(int position, List<Field> fields) {
        addProfileFieldForDetailsPage(position, Field.TYPE_MINI_FORM_PROFILE, fields);
    }

    protected void addProfileFieldForDetailsPage(int position, String miniFormType, List<Field> fields) {
        if (fields.isEmpty()) {
            return;
        }

        if (((RecordActivity) getActivity()).getCurrentFeature().isDetailMode()) {
            Field field = new Field();
            field.setType(miniFormType);
            try {
                fields.add(position, field);
            } catch (Exception e) {
                fields.add(field);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 1)
    public void updateImageAdapter(UpdateImageEvent event) {
        recordRegisterAdapter.getPhotoAdapter().addItem(event.getImagePath());
        recordRegisterAdapter.getPhotoAdapter().notifyDataSetChanged();
        EventBus.getDefault().removeStickyEvent(event);
    }

    @Override
    public void onRequiredFieldNotFilled() {
        Utils.showMessageByToast(getActivity(), R.string.required_field_is_not_filled, Toast.LENGTH_LONG);
    }

    @Override
    public void onSavedFail() {
        Utils.showMessageByToast(getActivity(), R.string.save_failed, Toast.LENGTH_SHORT);
    }

    public RecordPhotoAdapter getPhotoAdapter() {
        return recordRegisterAdapter.getPhotoAdapter();
    }

    protected void onFirstTimeLaunched() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveStateToArguments();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        saveStateToArguments();
    }

    @Override
    public void onFieldValueInvalid() {
        ItemValuesMap fieldValueVerifyResult = getFieldValueVerifyResult();
        MessageDialog messageDialog = new MessageDialog(getContext());
        messageDialog.setTitle(R.string.cannot_save);
        String errorMsg = generateFileValueInvalidMsg(fieldValueVerifyResult);
        messageDialog.setMessageColor(getContext().getResources().getColor(R.color.primero_font_medium));
        messageDialog.setMessageTextSize(R.dimen.text_size_6);
        messageDialog.setMessage(errorMsg);
        messageDialog.setPositiveButton(R.string.ok, view -> messageDialog.dismiss());
        messageDialog.show();
    }

    private String generateFileValueInvalidMsg(ItemValuesMap fieldValueVerifyResult) {
        StringBuilder sb = new StringBuilder("");
        Map<String, Object> values = fieldValueVerifyResult.getValues();
        HashMap<String, List<String>> invalidMsgMap = generateInvalidShowMsgMap(values);
        for (Map.Entry<String, List<String>> entry : invalidMsgMap.entrySet()) {
            sb.append(entry.getKey() + "\n\n");
            sb.append("Please check below fields:\n");
            for (String invalidMsg : entry.getValue()) {
                sb.append("- "+invalidMsg + "\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private HashMap<String, List<String>> generateInvalidShowMsgMap(Map<String, Object> values) {
        HashMap<String, List<String>> invalidShowMsgMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            LinkedHashMap<String, String> entryVal = (LinkedHashMap<String, String>) entry.getValue();
            for (Map.Entry<String, String> valueEntry : entryVal.entrySet()) {
                List<String> invalidEntryList = invalidShowMsgMap.containsKey(valueEntry.getValue()) ?
                        invalidShowMsgMap.get(valueEntry.getValue()) : new ArrayList<>();
                String invalidEntry = entry.getKey() + " > " + valueEntry.getKey();
                if (!invalidEntryList.contains(invalidEntry)) {
                    invalidEntryList.add(invalidEntry);
                }
                invalidShowMsgMap.put(valueEntry.getValue(), invalidEntryList);
            }
        }
        return invalidShowMsgMap;
    }

    private void saveStateToArguments() {
        if (getView() != null)
            savedState = saveState();
        if (savedState != null) {
            Bundle b = getArguments();
            b.putBundle(SAVED_STATE_ID, savedState);
        }
    }

    private boolean restoreStateFromArguments() {
        Bundle b = getArguments();
        if (getArguments() == null) {
            return false;
        }
        savedState = b.getBundle(SAVED_STATE_ID);
        if (savedState != null) {
            restoreState();
            return true;
        }
        return false;
    }

    private void restoreState() {
        if (savedState != null) {
            onRestoreState(savedState);
        }
    }

    protected void onRestoreState(Bundle savedInstanceState) {
        recordRegisterAdapter.setSubformDropDownStatus((HashMap<Integer, List<Boolean>>) savedInstanceState.getSerializable(SUBFORM_STATE));
    }

    private Bundle saveState() {
        Bundle state = new Bundle();
        onSaveState(state);
        return state;
    }

    protected void onSaveState(Bundle outState) {
        outState.putSerializable(SUBFORM_STATE, recordRegisterAdapter.getSubformDropDownStatus());
    }

    protected abstract RecordRegisterAdapter createRecordRegisterAdapter();
}