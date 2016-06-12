package org.unicef.rapidreg.childcase;

import com.hannesdorfmann.mosby.mvp.MvpView;

public interface CasesListView extends MvpView{

    void initView(CasesListAdapter adapter);

}
