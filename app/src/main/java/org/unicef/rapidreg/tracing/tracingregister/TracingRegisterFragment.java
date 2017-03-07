package org.unicef.rapidreg.tracing.tracingregister;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.Feature;
import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterAdapter;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterFragment;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.tracing.TracingFeature;
import org.unicef.rapidreg.tracing.tracingphoto.TracingPhotoAdapter;
import org.unicef.rapidreg.utils.Utils;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.OnClick;

public class TracingRegisterFragment extends RecordRegisterFragment {

    @Inject
    TracingRegisterPresenter tracingRegisterPresenter;

    @Inject
    TracingPhotoAdapter tracingPhotoAdapter;

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
        RecordRegisterAdapter recordRegisterAdapter = new RecordRegisterAdapter(getActivity(),
                tracingRegisterPresenter.getValidFields(FragmentPagerItem.getPosition(getArguments())),
                tracingRegisterPresenter.getDefaultItemValues(),
                tracingRegisterPresenter.getFieldValueVerifyResult(),
                false);

        tracingPhotoAdapter.setItems(tracingRegisterPresenter.getDefaultPhotoPaths());
        recordRegisterAdapter.setPhotoAdapter(tracingPhotoAdapter);

        return recordRegisterAdapter;
    }

    @Override
    public TracingRegisterPresenter createPresenter() {
        return tracingRegisterPresenter;
    }

    @OnClick(R.id.form_switcher)
    public void onSwitcherChecked() {
        Bundle args = new Bundle();
        args.putStringArrayList(RecordService.RECORD_PHOTOS, (ArrayList<String>) getPhotoPathsData());
        args.putSerializable(RecordService.ITEM_VALUES, getRecordRegisterData());
        args.putSerializable(RecordService.VERIFY_MESSAGE, getFieldValueVerifyResult());
        Feature feature = ((RecordActivity) getActivity()).getCurrentFeature().isDetailMode() ?
                TracingFeature.DETAILS_MINI : ((RecordActivity) getActivity()).getCurrentFeature().isAddMode() ?
                TracingFeature.ADD_MINI : TracingFeature.EDIT_MINI;
        ((RecordActivity) getActivity()).turnToFeature(feature, args, ANIM_TO_MINI);
    }

    @Override
    public void onSaveSuccessful(long recordId) {
        Utils.showMessageByToast(getActivity(), R.string.save_success, Toast.LENGTH_SHORT);
    }
}
