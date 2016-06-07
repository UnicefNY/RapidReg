package org.unicef.rapidreg.childcase;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.MvpFragment;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.forms.childcase.CaseField;
import org.unicef.rapidreg.forms.childcase.CaseFormRoot;
import org.unicef.rapidreg.forms.childcase.CaseSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CasesRegisterFragment extends MvpFragment<CasesRegisterView, CasesRegisterPresenter>
        implements CasesRegisterView {

    private static final String TAG = CasesRegisterFragment.class.getSimpleName();

    @BindView(R.id.fragment_register_content)
    LinearLayout registerContent;
    @BindView(R.id.register_forms_content)
    ExpandableListView formsContent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
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
    public void initView(CasesRegisterAdapter adapter, final CaseFormRoot caseFormRootRoot) {
        formsContent.setAdapter(adapter);
        formsContent.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                        int childPosition, long id) {
                List<CaseSection> sections = caseFormRootRoot.getSections();
                List<CaseField> formCaseFormFields = sections.get(groupPosition).getFields();
                CaseField field = formCaseFormFields.get(childPosition);
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

    private void showFieldDialog(CaseField field) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        initDialogButton(builder);
        builder.setTitle(field.getDisplayName().get("en"));
        String fieldType = field.getType();
        String[] optionItems = new String[0];
        if (fieldType.equals("select_box")) {
            fieldType = field.isMultiSelect() ? "multi_select_box" : "single_select_box";
            optionItems = getSelectOptions(fieldType, field);
        }
        Log.i(TAG, fieldType);
        Toast.makeText(getContext(), fieldType, Toast.LENGTH_SHORT).show();
        switch (fieldType) {
            case "text_field":
                EditText textSingle = new EditText(getContext());
                builder.setView(textSingle);
                break;

            case "textarea":
                EditText textMultiple = new EditText(getContext());
                textMultiple.setSingleLine(false);
                builder.setView(textMultiple);
                break;

            case "radio_button":
                Log.i(TAG, field.toString());
                optionItems = getSelectOptions(fieldType, field);

            case "single_select_box":
                builder.setSingleChoiceItems(optionItems, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                break;

            case "multi_select_box":
                builder.setMultiChoiceItems(optionItems, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                            }
                        });
                break;


            case "numeric_field":
                final EditText input = new EditText(getContext());
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setRawInputType(Configuration.KEYBOARD_12KEY);
                builder.setView(input);
                break;

            case "date_field":
                DatePicker picker = new DatePicker(getContext());
                picker.setCalendarViewShown(false);
                builder.setView(picker);
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
                dialog.dismiss();
            }
        });
    }

    private String[] getSelectOptions(String fieldType, CaseField field) {
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
