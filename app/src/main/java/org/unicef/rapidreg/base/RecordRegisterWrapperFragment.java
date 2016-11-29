package org.unicef.rapidreg.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentStatePagerItemAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.event.UpdateImageEvent;
import org.unicef.rapidreg.forms.RecordForm;
import org.unicef.rapidreg.forms.Section;
import org.unicef.rapidreg.injection.component.DaggerFragmentComponent;
import org.unicef.rapidreg.injection.component.FragmentComponent;
import org.unicef.rapidreg.injection.module.FragmentModule;
import org.unicef.rapidreg.service.cache.ItemValues;
import org.unicef.rapidreg.service.cache.ItemValuesMap;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class RecordRegisterWrapperFragment extends Fragment {
    public static final String ITEM_VALUES = "item_values";

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.viewpagertab)
    SmartTabLayout viewPagerTab;

    @BindView(R.id.edit)
    FloatingActionButton editButton;

    protected ItemValuesMap itemValues;
    protected RecordForm form;
    protected List<Section> sections;
    protected RecordPhotoAdapter recordPhotoAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_register_wrapper, container, false);
        ButterKnife.bind(this, view);

        initItemValues();
        initFormData();
        initFloatingActionButton();
        initRegisterContainer();

        return view;
    }

    public FragmentComponent getComponent() {
        return DaggerFragmentComponent.builder()
                .applicationComponent(PrimeroApplication.get(getContext()).getComponent())
                .fragmentModule(new FragmentModule(this))
                .build();
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
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                RecordRegisterFragment currentPage = (RecordRegisterFragment) adapter.getPage(position);

                RecordRegisterAdapter registerAdapter = currentPage.getRegisterAdapter();
                recordPhotoAdapter = registerAdapter.getPhotoAdapter();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    protected void clearProfileItems() {
        itemValues.removeItem(ItemValues.RecordProfile.ID_NORMAL_STATE);
        itemValues.removeItem(ItemValues.RecordProfile.REGISTRATION_DATE);
        itemValues.removeItem(ItemValues.RecordProfile.ID);
    }

    protected abstract void initItemValues();

    protected abstract void initFormData();

    protected abstract FragmentPagerItems getPages();
}
