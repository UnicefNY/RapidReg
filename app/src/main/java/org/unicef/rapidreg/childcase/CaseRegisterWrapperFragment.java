package org.unicef.rapidreg.childcase;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentStatePagerItemAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.view.SwipeChangeLayout;
import org.unicef.rapidreg.childcase.media.CasePhotoAdapter;
import org.unicef.rapidreg.event.SaveCaseEvent;
import org.unicef.rapidreg.event.UpdateImageEvent;
import org.unicef.rapidreg.forms.CaseFormRoot;
import org.unicef.rapidreg.forms.Field;
import org.unicef.rapidreg.forms.Section;
import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.model.CasePhoto;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.CasePhotoService;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.service.cache.ItemValues;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    @BindView(R.id.edit_case)
    FloatingActionButton editCaseButton;

    private CaseFormRoot caseForm;
    private List<Section> sections;
    private List<Field> miniFields;
    private CaseRegisterAdapter miniFormAdapter;
    private CaseRegisterAdapter fullFormAdapter;
    private CasePhotoAdapter casePhotoAdapter;

    private ItemValues itemValues;

    private long caseId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_cases_register_wrapper, container, false);
        ButterKnife.bind(this, view);

        if (getArguments() != null) {
            caseId = getArguments().getLong("case_id");
            Case caseItem = CaseService.getInstance().getCaseById(caseId);
            String caseJson = new String(caseItem.getContent().getBlob());
            String subFormJson = new String(caseItem.getSubform().getBlob());
            itemValues = CaseService.getInstance().generateItemValues(caseJson, subFormJson);
            itemValues.addStringItem(CaseService.CASE_ID, caseItem.getUniqueId());
            itemValues.addStringItem(CaseService.CASE_ID, caseItem.getUniqueId());
            initProfile(caseItem);
        }
        if (itemValues == null) {
            itemValues = new ItemValues();
        }

        initCaseFormData();
        initFloatingActionButton();

        miniFormAdapter = new CaseRegisterAdapter(getActivity(), miniFields, itemValues, true);
        miniFormAdapter.setCasePhotoAdapter(initCasePhotoAdapter());

        initFullFormContainer();
        initMiniFormContainer();

        return view;
    }

    private void initProfile(Case caseItem) {
        DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
        String shortUUID = CaseService.getInstance().getShortUUID(caseItem.getUniqueId());
        itemValues.addStringItem(ItemValues.CaseProfile.ID_NORMAL_STATE,
                shortUUID);
        itemValues.addStringItem(ItemValues.CaseProfile.REGISTRATION_DATE,
                dateFormat.format(caseItem.getRegistrationDate()));
        itemValues.addStringItem(ItemValues.CaseProfile.GENDER_NAME,
                shortUUID);
        itemValues.addNumberItem(ItemValues.CaseProfile.ID, caseItem.getId());
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

    @OnClick(R.id.edit_case)
    public void onCaseEditClicked() {
        ((CaseActivity) getActivity()).turnToDetailOrEditPage(CaseFeature.EDIT, caseId);
    }

    private CasePhotoAdapter initCasePhotoAdapter() {
        casePhotoAdapter = new CasePhotoAdapter(getContext(), new ArrayList<String>());

        List<CasePhoto> casesPhotoFlowQueryList = CasePhotoService.getInstance().getByCaseId(caseId);
        for (int i = 0; i < casesPhotoFlowQueryList.size(); i++) {
            casePhotoAdapter.addItem(casesPhotoFlowQueryList.get(i).getId());
        }
        return casePhotoAdapter;
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 1)
    public void updateImageAdapter(UpdateImageEvent event) {
        casePhotoAdapter.addItem(event.getImagePath());
        ImageButton view = (ImageButton) getActivity().findViewById(R.id.add_image_button);

        if (!casePhotoAdapter.isEmpty()) {
            view.setImageResource(R.drawable.photo_add);
        }
        if (casePhotoAdapter.isFull()) {
            view.setVisibility(View.GONE);
        }

        casePhotoAdapter.notifyDataSetChanged();
        EventBus.getDefault().removeStickyEvent(event);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void saveCase(SaveCaseEvent event) {
        if (validateRequiredField()) {
            List<String> photoPaths = casePhotoAdapter.getAllItems();
            CaseService.getInstance().saveOrUpdate(itemValues, photoPaths);
        }
    }

    private boolean validateRequiredField() {
        CaseFormRoot caseForm = CaseFormService.getInstance().getCurrentForm();
        List<String> requiredFieldNames = new ArrayList<>();

        for (Section section : caseForm.getSections()) {
            Collections.addAll(requiredFieldNames, CaseService.getInstance()
                    .fetchRequiredFiledNames(section.getFields()).toArray(new String[0]));
        }

        for (String field : requiredFieldNames) {
            if (TextUtils.isEmpty((CharSequence) itemValues.getValues().get(field))) {
                Toast.makeText(getActivity(), R.string.required_field_is_not_filled,
                        Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    private void initFloatingActionButton() {
        if (((CaseActivity) getActivity()).getCurrentFeature() == CaseFeature.DETAILS) {
            editCaseButton.setVisibility(View.VISIBLE);
        } else {
            editCaseButton.setVisibility(View.GONE);
        }
    }

    private void initMiniFormContainer() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        if (!miniFields.isEmpty()) {
            miniFormContainer.setLayoutManager(layoutManager);
            miniFormContainer.setAdapter(miniFormAdapter);
            miniFormSwipeLayout.setDragEdge(SwipeChangeLayout.DragEdge.BOTTOM);
            miniFormSwipeLayout.setShouldGoneContainer(miniFormLayout);
            miniFormSwipeLayout.setShouldShowContainer(fullFormLayout);
            miniFormSwipeLayout.setScrollChild(miniFormContainer);
            miniFormSwipeLayout.setOnSwipeBackListener(new SwipeChangeLayout.SwipeBackListener() {
                @Override
                public void onViewPositionChanged(float fractionAnchor, float fractionScreen) {
                    if (fullFormAdapter != null) {
                        fullFormAdapter.setItemValues(itemValues);
                        fullFormAdapter.setCasePhotoAdapter(casePhotoAdapter);
                    }
                }
            });
        } else {
            miniFormSwipeLayout.setEnableFlingBack(false);
            miniFormLayout.setVisibility(View.GONE);
            fullFormLayout.setVisibility(View.VISIBLE);
            fullFormSwipeLayout.setEnableFlingBack(false);
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
                CaseRegisterFragment currentPage = (CaseRegisterFragment) adapter.getPage(position);

                itemValues = currentPage.getItemValues();
                fullFormAdapter = currentPage.getCaseRegisterAdapter();
                fullFormAdapter.setItemValues(itemValues);
                fullFormAdapter.setCasePhotoAdapter(casePhotoAdapter);
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
                    miniFormAdapter.notifyDataSetChanged();
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
        for (Section section : sections) {
            for (Field field : section.getFields()) {
                if (field.isShowOnMiniForm()) {
                    if (field.isPhotoUploadBox()) {
                        miniFields.add(0, field);
                    } else {
                        miniFields.add(field);
                    }
                }
            }
        }
        addProfileFieldForDetailsPage();
    }

    private void addProfileFieldForDetailsPage() {
        if (((CaseActivity) getActivity()).getCurrentFeature() == CaseFeature.DETAILS) {
            Field field = new Field();
            field.setType(Field.TYPE_MINI_FORM_PROFILE);
            try {
                miniFields.add(1, field);
            } catch (Exception e) {
                miniFields.add(field);
            }
        }
    }

    @NonNull
    private FragmentPagerItems getPages() {
        FragmentPagerItems pages = new FragmentPagerItems(getActivity());
        for (Section section : sections) {
            String[] values = section.getName().values().toArray(new String[0]);
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("case_photos",
                    (ArrayList<String>) casePhotoAdapter.getAllItems());
            bundle.putString("item_values", new Gson().toJson(itemValues.getValues()));
            pages.add(FragmentPagerItem.of(values[0], CaseRegisterFragment.class, bundle));
        }
        return pages;
    }

    public void clearFocus() {
        View focusedChild = miniFormContainer.getFocusedChild();
        if (focusedChild != null) {
            focusedChild.clearFocus();
        }

        FragmentStatePagerItemAdapter adapter =
                (FragmentStatePagerItemAdapter) viewPager.getAdapter();
        CaseRegisterFragment fragment = (CaseRegisterFragment) adapter
                .getPage(viewPager.getCurrentItem());
        if (fragment != null) {
            fragment.clearFocus();
        }
    }
}
