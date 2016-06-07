package org.unicef.rapidreg.childcase;

import com.hannesdorfmann.mosby.mvp.MvpView;

import org.unicef.rapidreg.forms.childcase.CaseFormRoot;

public interface CasesRegisterView extends MvpView {

    void initView(CasesRegisterAdapter adapter);
}
