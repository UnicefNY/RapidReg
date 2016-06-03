package org.unicef.rapidreg.cases;

import com.hannesdorfmann.mosby.mvp.MvpView;

import org.unicef.rapidreg.model.forms.cases.CaseFormBean;

public interface CasesRegisterView extends MvpView {

    void initView(CasesRegisterAdapter adapter, CaseFormBean form);

    void expandAll(CasesRegisterAdapter adapter);
}
