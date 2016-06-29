package org.unicef.rapidreg.childcase;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentStatePagerItemAdapter;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.view.SwipeChangeLayout;
import org.unicef.rapidreg.forms.childcase.CaseField;
import org.unicef.rapidreg.forms.childcase.CaseFormRoot;
import org.unicef.rapidreg.forms.childcase.CaseSection;
import org.unicef.rapidreg.service.CaseFormService;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CaseRegisterWrapperFragment extends Fragment {

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.viewpagertab)
    SmartTabLayout viewPagerTab;

    @BindView(R.id.mini_form_layout)
    RelativeLayout miniFormLayout;

    @BindView(R.id.full_form_layout)
    RelativeLayout fullFormLayout;

    @BindView(R.id.full_form_swipe_layout)
    SwipeChangeLayout fullFormSwipeLayout;

    @BindView(R.id.mini_form_swipe_layout)
    SwipeChangeLayout miniFormSwipeLayout;

    @BindView(R.id.mini_form_container)
    RecyclerView miniFormContainer;

    private CaseFormRoot caseForm;
    private List<CaseSection> sections;
    private List<CaseField> miniFields;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_cases_register_wrapper, container, false);
        ButterKnife.bind(this, view);
        initCaseFormData();
        initMiniFormContainer();
        initFullFormContainer();
        return view;
    }

    private void initMiniFormContainer() {
        CaseRegisterAdapter caseRegisterAdapter =
                new CaseRegisterAdapter(getActivity(), miniFields, true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        if (miniFields.size() != 0) {
            miniFormContainer.setLayoutManager(layoutManager);
            miniFormContainer.setAdapter(caseRegisterAdapter);
            miniFormSwipeLayout.setDragEdge(SwipeChangeLayout.DragEdge.BOTTOM);
            miniFormSwipeLayout.setShouldGoneContainer(miniFormLayout);
            miniFormSwipeLayout.setShouldShowContainer(fullFormLayout);
            miniFormSwipeLayout.setScrollChild(miniFormContainer);
            miniFormSwipeLayout.setOnSwipeBackListener(new SwipeChangeLayout.SwipeBackListener() {
                @Override
                public void onViewPositionChanged(float fractionAnchor, float fractionScreen) {

                }
            });
        } else {
            miniFormSwipeLayout.setEnableFlingBack(false);
            miniFormLayout.setVisibility(View.GONE);
            fullFormLayout.setVisibility(View.VISIBLE);
        }
    }

    private void initFullFormContainer() {
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
                fullFormSwipeLayout.setScrollChild(
                        adapter.getPage(position).getView()
                                .findViewById(R.id.register_forms_content));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if (miniFields.size() != 0) {
            fullFormSwipeLayout.setDragEdge(SwipeChangeLayout.DragEdge.TOP);
            fullFormSwipeLayout.setShouldGoneContainer(fullFormLayout);
            fullFormSwipeLayout.setShouldShowContainer(miniFormLayout);
            fullFormSwipeLayout.setOnSwipeBackListener(new SwipeChangeLayout.SwipeBackListener() {
                @Override
                public void onViewPositionChanged(float fractionAnchor, float fractionScreen) {

                }
            });
        } else {
            fullFormSwipeLayout.setEnableFlingBack(false);
        }
    }

    private void initCaseFormData() {
        caseForm = CaseFormService.getInstance().getCurrentForm();
        sections = caseForm.getSections();
        miniFields = new ArrayList<>();
        if (caseForm != null) {
            getMiniFields();
        }
    }

    private void getMiniFields() {
        for (CaseSection section : sections) {
            for (CaseField caseField : section.getFields()) {
                if (caseField.isShowOnMiniForm()) {
                    if (caseField.isPhotoUploadBox()) {
                        miniFields.add(0, caseField);
                    } else {
                        miniFields.add(caseField);
                    }
                }
            }
        }
    }

    @NonNull
    private FragmentPagerItems getPages() {
        FragmentPagerItems pages = new FragmentPagerItems(getActivity());
        for (CaseSection section : sections) {
            String[] values = section.getName().values().toArray(new String[0]);
            pages.add(FragmentPagerItem.of(values[0], CaseRegisterFragment.class));
        }
        return pages;
    }
}
