package org.unicef.rapidreg.childcase;

import com.hannesdorfmann.mosby.mvp.MvpView;

public interface CaseListView extends MvpView {
    void initView(CaseListAdapter adapter);
}
