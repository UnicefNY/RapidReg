package org.unicef.rapidreg.tracing.tracingregister;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterAdapter;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterWrapperFragment;
import org.unicef.rapidreg.event.SaveTracingEvent;
import org.unicef.rapidreg.forms.Section;
import org.unicef.rapidreg.forms.TracingFormRoot;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.TracingFormService;
import org.unicef.rapidreg.service.cache.ItemValues;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.tracing.TracingActivity;
import org.unicef.rapidreg.tracing.TracingFeature;
import org.unicef.rapidreg.tracing.tracingphoto.TracingPhotoAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.OnClick;

public class TracingRegisterWrapperFragment extends RecordRegisterWrapperFragment {
    public static final String TAG = TracingRegisterWrapperFragment.class.getSimpleName();

    @Inject
    TracingRegisterPresenter tracingRegisterPresenter;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void saveTracing(SaveTracingEvent event) {
        tracingRegisterPresenter.saveRecord(itemValues);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getComponent().inject(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @OnClick(R.id.edit)
    public void onEditClicked() {
        Bundle args = new Bundle();
        args.putSerializable(RecordService.ITEM_VALUES, itemValues);
        args.putStringArrayList(RecordService.RECORD_PHOTOS, (ArrayList<String>) recordPhotoAdapter.getAllItems());
        ((TracingActivity) getActivity()).turnToFeature(TracingFeature.EDIT_FULL, args, null);
    }

    @Override
    protected void initItemValues() {
        if (getArguments() != null) {
            itemValues = (ItemValuesMap) getArguments().getSerializable(ITEM_VALUES);
            recordPhotoAdapter = new TracingPhotoAdapter(getContext(),
                    getArguments().getStringArrayList(RecordService.RECORD_PHOTOS));
        }
    }

    @Override
    protected void initFormData() {
        form = tracingRegisterPresenter.getCurrentForm();
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
            pages.add(FragmentPagerItem.of(values[0], TracingRegisterFragment.class, args));
        }
        return pages;
    }

    private boolean validateRequiredField() {
        TracingFormRoot tracingForm = TracingFormService.getInstance().getCurrentForm();
        return RecordService.validateRequiredFields(tracingForm, itemValues);
    }

    @Override
    public TracingRegisterPresenter createPresenter() {
        return tracingRegisterPresenter;
    }

    @Override
    public void initView(RecordRegisterAdapter adapter) {

    }

    @Override
    public void saveSuccessfully(long recordId) {
        Bundle args = new Bundle();
        args.putSerializable(RecordService.ITEM_VALUES, itemValues);
        args.putStringArrayList(RecordService.RECORD_PHOTOS, (ArrayList<String>) getPhotos());
        ((RecordActivity) getActivity()).turnToFeature(TracingFeature.DETAILS_FULL, args, null);
    }

}
