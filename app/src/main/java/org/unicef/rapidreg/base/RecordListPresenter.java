package org.unicef.rapidreg.base;

import android.content.Context;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import org.unicef.rapidreg.base.RecordListFragment.SpinnerState;
import org.unicef.rapidreg.childcase.CaseListAdapter;
import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.TracingService;
import org.unicef.rapidreg.tracing.TracingListAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class RecordListPresenter extends MvpBasePresenter<RecordListView> {

    RecordService recordService;

    private int type;

    @Inject
    public RecordListPresenter(RecordService recordService) {
        this.recordService = recordService;
    }

    public RecordListPresenter(int type) {
        this.type = type;
    }

    public void initView(Context context) {}


    public void clearAudioFile() {
        recordService.clearAudioFile();
    }
}
