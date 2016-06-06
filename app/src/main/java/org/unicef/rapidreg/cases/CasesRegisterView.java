package org.unicef.rapidreg.cases;

import com.hannesdorfmann.mosby.mvp.MvpView;

import org.unicef.rapidreg.model.form.childcase.CaseForm;

public interface CasesRegisterView extends MvpView {

    void initView(CasesRegisterAdapter adapter, CaseForm form);

    void expandAll(CasesRegisterAdapter adapter);
}
