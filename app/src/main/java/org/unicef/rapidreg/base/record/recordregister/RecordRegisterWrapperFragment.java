package org.unicef.rapidreg.base.record.recordregister;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.MvpFragment;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentStatePagerItemAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.base.record.recordphoto.RecordPhotoAdapter;
import org.unicef.rapidreg.event.UpdateImageEvent;
import org.unicef.rapidreg.forms.RecordForm;
import org.unicef.rapidreg.forms.Section;
import org.unicef.rapidreg.injection.component.DaggerFragmentComponent;
import org.unicef.rapidreg.injection.component.FragmentComponent;
import org.unicef.rapidreg.injection.module.FragmentModule;
import org.unicef.rapidreg.service.cache.ItemValuesMap;
import org.unicef.rapidreg.utils.Utils;
import org.unicef.rapidreg.widgets.dialog.MessageDialog;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class RecordRegisterWrapperFragment extends MvpFragment<RecordRegisterView,
        RecordRegisterPresenter>
        implements RecordRegisterView, RecordRegisterView.SaveRecordCallback {
    private static final String TAG = RecordRegisterWrapperFragment.class.getSimpleName();

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.viewpagertab)
    SmartTabLayout viewPagerTab;

    @BindView(R.id.edit)
    FloatingActionButton editButton;

    protected RecordForm form;
    protected List<Section> sections;

    protected RecordPhotoAdapter recordPhotoAdapter;

    private ItemValuesMap itemValues;
    private ItemValuesMap itemValuesVerifyList;

    public FragmentComponent getComponent() {
        return DaggerFragmentComponent.builder()
                .applicationComponent(PrimeroApplication.get(getContext()).getComponent())
                .fragmentModule(new FragmentModule(this))
                .build();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_register_wrapper, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onInitViewContent();
        initItemValues();
        initFormData();
        initFloatingActionButton();
        initRegisterContainer();
    }

    @Override
    public void onInitViewContent() {
        recordPhotoAdapter = createRecordPhotoAdapter();
    }

    @Override
    public void setRecordRegisterData(ItemValuesMap itemValues) {
        this.itemValues = itemValues;
    }

    @Override
    public void setPhotoPathsData(List<String> photoPaths) {
        recordPhotoAdapter.setItems(photoPaths);
    }

    @Override
    public List<String> getPhotoPathsData() {
        if (recordPhotoAdapter == null) {
            return Collections.EMPTY_LIST;
        }
        return recordPhotoAdapter.getAllItems();
    }

    @Override
    public ItemValuesMap getRecordRegisterData() {
        return itemValues;
    }

    @Override
    public void setFieldValueVerifyResult(ItemValuesMap fieldValueVerifyResult) {
        this.itemValuesVerifyList = fieldValueVerifyResult;
    }

    @Override
    public ItemValuesMap getFieldValueVerifyResult() {
        return itemValuesVerifyList;
    }


    @Override
    public void onRequiredFieldNotFilled() {
        Utils.showMessageByToast(getActivity(), R.string.required_field_is_not_filled, Toast.LENGTH_LONG);
    }

    @Override
    public void onSavedFail() {
        Utils.showMessageByToast(getActivity(), R.string.save_failed, Toast.LENGTH_SHORT);
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

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 1)
    public void updateImageAdapter(UpdateImageEvent event) {
        recordPhotoAdapter.addItem(event.getImagePath());
        recordPhotoAdapter.notifyDataSetChanged();
        EventBus.getDefault().removeStickyEvent(event);
    }

    private void initFloatingActionButton() {
        if (((RecordActivity) getActivity()).getCurrentFeature().isDetailMode()) {
            editButton.setVisibility(View.VISIBLE);
        } else {
            editButton.setVisibility(View.GONE);
        }
    }

    private void initRegisterContainer() {
        final FragmentStatePagerItemAdapter adapter = new FragmentStatePagerItemAdapter(
                getActivity().getSupportFragmentManager(), getPages());
        viewPager.setAdapter(adapter);
        viewPagerTab.setViewPager(viewPager);
        viewPagerTab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int
                    positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                RecordRegisterFragment currentPage = (RecordRegisterFragment) adapter.getPage
                        (position);
                if (recordPhotoAdapter != null) {
                    currentPage.setFieldValueVerifyResult(itemValuesVerifyList);
                    recordPhotoAdapter = currentPage.getPhotoAdapter();
                    recordPhotoAdapter.setItems(currentPage.getPhotoPathsData());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onFieldValueInvalid() {
        ItemValuesMap fieldValueVerifyResult = getFieldValueVerifyResult();
        MessageDialog messageDialog = new MessageDialog(getContext());
        messageDialog.setTitle(R.string.invalid_value);
        String errorMsg = generateFileValueInvalidMsg(fieldValueVerifyResult);
        messageDialog.setMessage(errorMsg);
        messageDialog.setPositiveButton(R.string.ok, view -> messageDialog.dismiss());
        messageDialog.show();
    }

    private String generateFileValueInvalidMsg(ItemValuesMap fieldValueVerifyResult) {
        StringBuilder sb = new StringBuilder("");
        Map<String, Object> values = fieldValueVerifyResult.getValues();
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            sb.append("[" + entry.getKey() + "]\n");
            LinkedHashMap<String, String> entryVal = (LinkedHashMap<String, String>) entry.getValue();
            for (Map.Entry<String, String> valueEntry : entryVal.entrySet()) {
                sb.append(valueEntry.getKey());
                sb.append(": " + valueEntry.getValue());
                sb.append("\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    protected abstract RecordPhotoAdapter createRecordPhotoAdapter();

    protected abstract void initItemValues();

    protected abstract void initFormData();

    protected abstract FragmentPagerItems getPages();
}
