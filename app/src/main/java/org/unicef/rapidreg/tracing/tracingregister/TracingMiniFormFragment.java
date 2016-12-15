package org.unicef.rapidreg.tracing.tracingregister;

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
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterAdapter;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterFragment;
import org.unicef.rapidreg.event.SaveTracingEvent;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.TracingService;
import org.unicef.rapidreg.tracing.TracingActivity;
import org.unicef.rapidreg.tracing.TracingFeature;
import org.unicef.rapidreg.tracing.tracingphoto.TracingPhotoAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.OnClick;

import static org.unicef.rapidreg.db.impl.TracingDaoImpl.TRACING_PRIMARY_ID;

public class TracingMiniFormFragment extends RecordRegisterFragment {

    public static final String TAG = TracingMiniFormFragment.class.getSimpleName();

    @Inject
    TracingRegisterPresenter tracingRegisterPresenter;

    @Inject
    TracingPhotoAdapter tracingPhotoAdapter;

    @NonNull
    @Override
    public TracingRegisterPresenter createPresenter() {
        return tracingRegisterPresenter;
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
    protected RecordRegisterAdapter createRecordRegisterAdapter() {
        List<Field> fields = tracingRegisterPresenter.getValidFields();
        addProfileFieldForDetailsPage(0, fields);

        RecordRegisterAdapter recordRegisterAdapter = new RecordRegisterAdapter(getActivity(),
                fields,
                tracingRegisterPresenter.getDefaultItemValues(),
                true);

        tracingPhotoAdapter.setItems(tracingRegisterPresenter.getDefaultPhotoPaths());
        recordRegisterAdapter.setPhotoAdapter(tracingPhotoAdapter);

        return recordRegisterAdapter;
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
    public void onInitViewContent() {
        super.onInitViewContent();
        formSwitcher.setText(R.string.show_more_details);

        if (((RecordActivity) getActivity()).getCurrentFeature().isDetailMode()) {
            editButton.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void saveTracing(SaveTracingEvent event) {
        tracingRegisterPresenter.saveRecord(getRecordRegisterData(), getPhotoPathsData(), this);
    }

    @Override
    public void onSaveSuccessful(long recordId) {
        Bundle args = new Bundle();
        args.putLong(TRACING_PRIMARY_ID, recordId);
        Toast.makeText(getActivity(), R.string.save_success, Toast.LENGTH_SHORT).show();
        ((RecordActivity) getActivity()).turnToFeature(TracingFeature.DETAILS_MINI, args, null);
    }

    @OnClick(R.id.edit)
    public void onEditClicked() {
        Bundle args = new Bundle();
        args.putSerializable(RecordService.ITEM_VALUES, getRecordRegisterData());
        args.putStringArrayList(RecordService.RECORD_PHOTOS, (ArrayList<String>) getPhotoPathsData());
        ((TracingActivity) getActivity()).turnToFeature(TracingFeature.EDIT_MINI, args, null);
    }

    @OnClick(R.id.form_switcher)
    public void onSwitcherChecked() {
        Bundle args = new Bundle();
        args.putSerializable(RecordService.ITEM_VALUES, getRecordRegisterData());
        args.putStringArrayList(RecordService.RECORD_PHOTOS, (ArrayList<String>) getPhotoPathsData());
        Feature feature = ((RecordActivity) getActivity()).getCurrentFeature().isDetailMode() ?
                TracingFeature.DETAILS_FULL : ((RecordActivity) getActivity()).getCurrentFeature().isAddMode() ?
                TracingFeature.ADD_FULL : TracingFeature.EDIT_FULL;
        ((RecordActivity) getActivity()).turnToFeature(feature, args, ANIM_TO_FULL);
    }
}
