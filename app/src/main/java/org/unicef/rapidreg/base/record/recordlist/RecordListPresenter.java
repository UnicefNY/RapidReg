package org.unicef.rapidreg.base.record.recordlist;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import org.unicef.rapidreg.base.record.recordlist.spinner.SpinnerState;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.utils.Utils;

import java.util.List;

import static org.unicef.rapidreg.service.RecordService.AUDIO_FILE_PATH;

public abstract class RecordListPresenter extends MvpBasePresenter<RecordListView> {

    private RecordService recordService;

    public RecordListPresenter(RecordService recordService) {
        this.recordService = recordService;
    }

    public void clearAudioFile() {
        Utils.clearAudioFile(AUDIO_FILE_PATH);
    }

    public abstract boolean isFormReady();

    public abstract int calculateDisplayedIndex();

    public abstract List<Long> getRecordsByFilter(SpinnerState spinnerState);
}
