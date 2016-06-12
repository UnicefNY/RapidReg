package org.unicef.rapidreg.childcase;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

public class CaseListPresenter extends MvpBasePresenter<CaseListView> {

    public void initView() {
        if (isViewAttached()) {
            getView().initView(new CaseListAdapter());
        }
    }
}
