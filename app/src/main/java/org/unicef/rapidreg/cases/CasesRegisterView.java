package org.unicef.rapidreg.cases;

import com.hannesdorfmann.mosby.mvp.MvpView;

public interface CasesRegisterView extends MvpView{

    public void initView(CasesRegisterAdapter adapter);
    public void expandAll(CasesRegisterAdapter adapter);
}
