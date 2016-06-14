package org.unicef.rapidreg.childcase;

import android.content.Context;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

public class CaseListPresenter extends MvpBasePresenter<CaseListView> {

    public void initView(Context context) {
        if (isViewAttached()) {
            getView().initView(new CaseListAdapter(context));
        }
    }
}
