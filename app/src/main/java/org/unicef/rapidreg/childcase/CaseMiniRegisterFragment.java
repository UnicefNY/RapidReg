package org.unicef.rapidreg.childcase;

import com.hannesdorfmann.mosby.mvp.MvpFragment;

public class CaseMiniRegisterFragment extends MvpFragment<CaseMiniRegisterView, CaseMiniRegisterPresenter>
        implements CaseMiniRegisterView{

    @Override
    public CaseMiniRegisterPresenter createPresenter() {
        return new CaseMiniRegisterPresenter();
    }
}
