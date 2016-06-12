package org.unicef.rapidreg.childcase;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

public class CasesListPresenter extends MvpBasePresenter<CasesListView> {

    public void initView() {
        if (isViewAttached()) {
            getView().initView(new CasesListAdapter());
        }
    }
}
