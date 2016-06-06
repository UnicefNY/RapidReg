package org.unicef.rapidreg.cases;

import com.hannesdorfmann.mosby.mvp.MvpView;

import org.unicef.rapidreg.form.childcase.CaseFormRoot;

public interface CasesRegisterView extends MvpView {

    void initView(CasesRegisterAdapter adapter, CaseFormRoot form);

    void expandAll(CasesRegisterAdapter adapter);
}
