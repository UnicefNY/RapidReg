package org.unicef.rapidreg.cases;

import com.hannesdorfmann.mosby.mvp.MvpView;

import org.unicef.rapidreg.model.form.childcase.CaseFormRoot;

public interface CasesRegisterView extends MvpView {

    void initView(CasesRegisterAdapter adapter, CaseFormRoot form);

    void expandAll(CasesRegisterAdapter adapter);
}
