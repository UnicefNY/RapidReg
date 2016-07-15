package org.unicef.rapidreg.base;

import com.hannesdorfmann.mosby.mvp.MvpView;

public interface RecordListView extends MvpView {
    void initView(RecordListAdapter adapter);
}
