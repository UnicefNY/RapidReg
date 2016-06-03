package org.unicef.rapidreg.cases;

import com.hannesdorfmann.mosby.mvp.MvpView;

import org.unicef.rapidreg.model.forms.CaseFormRoot;

public interface CasesRegisterView extends MvpView {

    public void initView(CasesRegisterAdapter adapter, CaseFormRoot caseFormRoot);

    public void expandAll(CasesRegisterAdapter adapter);
}
