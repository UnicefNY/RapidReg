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

    private List<CaseField> miniFields;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_cases_register_wrapper, container, false);
        ButterKnife.bind(this, view);

        FragmentStatePagerItemAdapter adapter = new FragmentStatePagerItemAdapter(
                getActivity().getSupportFragmentManager(), getPages());
        viewPager.setAdapter(adapter);
        viewPagerTab.setViewPager(viewPager);

        CaseFormRoot form = CaseFormService.getInstance().getCurrentForm();

        if (form != null) {
            miniFields = new ArrayList<>();
            for (CaseSection section : form.getSections()) {
                for (CaseField caseField : section.getFields()) {
                    if (caseField.isShowOnMiniForm()) {
                        miniFields.add(caseField);
                    }
                }
            }
//                getView().initView(new CaseMiniRegisterAdapter(context, -1, fields));
//            } else {
//                ((BaseActivity) context).getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.fragment_content, new CaseRegisterWrapperFragment())
//                        .addToBackStack(null)
//                        .commit();
//                Toast.makeText(context, "There is no mini form!", Toast.LENGTH_SHORT).show();
//            }
        }
        CaseRegisterAdapter caseRegisterAdapter = new CaseRegisterAdapter(getActivity(), miniFields);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        if (miniFields.size() != 0) {
            miniFormContainer.setLayoutManager(layoutManager);
            miniFormContainer.setAdapter(caseRegisterAdapter);
        }

        fullFormSwipeLayout.setDragEdge(SwipeChangeLayout.DragEdge.TOP);
        fullFormSwipeLayout.setShouldGoneContainer(fullFormLayout);
        fullFormSwipeLayout.setShouldShowContainer(miniFormLayout);
        fullFormSwipeLayout.setOnSwipeBackListener(new SwipeChangeLayout.SwipeBackListener() {
            @Override
            public void onViewPositionChanged(float fractionAnchor, float fractionScreen) {

            }
        });
        miniFormSwipeLayout.setDragEdge(SwipeChangeLayout.DragEdge.BOTTOM);
        miniFormSwipeLayout.setShouldGoneContainer(miniFormLayout);
        miniFormSwipeLayout.setShouldShowContainer(fullFormLayout);
        miniFormSwipeLayout.setOnSwipeBackListener(new SwipeChangeLayout.SwipeBackListener() {
            @Override
            public void onViewPositionChanged(float fractionAnchor, float fractionScreen) {

            }
        });

        return view;
    }

    @NonNull
    private FragmentPagerItems getPages() {
        CaseFormRoot caseForm = CaseFormService.getInstance().getCurrentForm();
        List<CaseSection> sections = caseForm.getSections();

        FragmentPagerItems pages = new FragmentPagerItems(getActivity());
        for (CaseSection section : sections) {
            String[] values = section.getName().values().toArray(new String[0]);
            pages.add(FragmentPagerItem.of(values[0], CaseRegisterFragment.class));
        }
        return pages;
    }
}
