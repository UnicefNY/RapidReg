package org.unicef.rapidreg.base.record.recordlist;

import android.content.Context;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import org.unicef.rapidreg.service.RecordService;

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
