package org.unicef.rapidreg.cases;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import com.hannesdorfmann.mosby.mvp.MvpFragment;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.model.forms.cases.CaseFieldBean;
import org.unicef.rapidreg.model.forms.cases.CaseFormBean;
import org.unicef.rapidreg.model.forms.cases.CaseSectionBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CasesRegisterFragment extends MvpFragment<CasesRegisterView, CasesRegisterPresenter> implements CasesRegisterView {

    @BindView(R.id.fragment_register_content)
    LinearLayout registerContent;
    @BindView(R.id.register_forms_content)
    ExpandableListView formsContent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_cases_register, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        presenter.initContext(getActivity());
    }

    @Override
    public CasesRegisterPresenter createPresenter() {
        return new CasesRegisterPresenter();
    }

    @Override
    public void initView(CasesRegisterAdapter adapter, final CaseFormBean caseFormRoot) {
        formsContent.setAdapter(adapter);
        formsContent.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                        int childPosition, long id) {
                List<CaseSectionBean> caseFormSections = caseFormRoot.getSections();
                List<CaseFieldBean> formCaseFormFields = caseFormSections.get(groupPosition).getFields();
                CaseFieldBean field = formCaseFormFields.get(childPosition);
                showFieldDialog(field);
                return false;
            }
        });
    }

    @Override
    public void expandAll(CasesRegisterAdapter adapter) {
        int count = adapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            formsContent.expandGroup(i);
        }
    }

    private void showFieldDialog(CaseFieldBean field) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        initDialogButton(builder);
        builder.setTitle(field.getDisplayName().get("en"));
        String fieldType = field.getType();
        String[] optionItems = new String[0];
        if (fieldType.equals("select_box")) {
            if (field.isMultiSelect()) {
                fieldType = "multi_select_box";
                optionItems = getSelectOptions(fieldType,field);
            } else {
                fieldType = "single_select_box";
                optionItems = getSelectOptions(fieldType,field);
            }
        }
        switch (fieldType) {
            case "text_field":
            case "textarea":
                builder.setView(R.layout.form_input);
                break;
            case "single_select_box":
                builder.setSingleChoiceItems(optionItems, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                break;
            case "multi_select_box":
                builder.setMultiChoiceItems(optionItems, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                    }
                });
                break;
            default:
                break;
        }
        builder.show();
    }

    private void initDialogButton(AlertDialog.Builder builder) {
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNeutralButton("Clear", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
    }

    private String[] getSelectOptions(String fieldType, CaseFieldBean field) {
        List<CharSequence> items = new ArrayList<>();
        if (fieldType.equals("multi_select_box")) {
            List<Map<String, String>> arrayList = field.getOptionStringsText().get("en");
            for (Map<String, String> map : arrayList) {
                items.add(map.get("display_text"));
            }
        } else {
            items = field.getOptionStringsText().get("en");
        }
        return items.toArray(new String[0]);
    }
}
