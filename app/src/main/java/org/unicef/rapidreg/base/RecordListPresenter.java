package org.unicef.rapidreg.base;

import android.content.Context;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import org.unicef.rapidreg.childcase.CaseListAdapter;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.tracing.TracingListAdapter;

public class RecordListPresenter extends MvpBasePresenter<RecordListView> {

    private int type;

    public RecordListPresenter(int type) {
        this.type = type;
    }

    public void initView(Context context) {
        if (isViewAttached()) {
            if (type == RecordModel.CASE) {
                getView().initView(new CaseListAdapter(context));
            } else if (type == RecordModel.TRACING) {
                getView().initView(new TracingListAdapter(context));
            }
        }
    }
}
