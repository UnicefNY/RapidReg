package org.unicef.rapidreg.cases;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.model.forms.CaseFormRoot;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CasesRegisterFragment extends Fragment {

    @BindView(R.id.fragment_register_content) LinearLayout registerContent;
    @BindView(R.id.register_forms_content) ExpandableListView formsContent;

    private CasesRegisterAdapter casesRegisterAdapter;
    private CaseFormRoot caseFormRoot;
    private PrimeroApplication primeroApplication;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_cases_register, container, false);
        ButterKnife.bind(this, view);
        primeroApplication = (PrimeroApplication) getActivity().getApplicationContext();
        if ((caseFormRoot = loadCaseForms()) != null) {
            casesRegisterAdapter = new CasesRegisterAdapter(getActivity(), caseFormRoot.getCaseFormSections());//.getFormSections());
            formsContent.setAdapter(casesRegisterAdapter);
            expandAll();
        };
        return view;
    }

    private CaseFormRoot loadCaseForms() {
        return primeroApplication.getCaseFormSections();
    }

    private void expandAll() {
        int count = casesRegisterAdapter.getGroupCount();
        for (int i = 0; i < count; i++){
            formsContent.expandGroup(i);
        }
    }


}
