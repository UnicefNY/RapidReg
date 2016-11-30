package org.unicef.rapidreg.base.record.recordsearch;

import android.content.Context;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import org.unicef.rapidreg.base.record.recordlist.RecordListView;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.inject.Inject;

public class RecordSearchPresenter extends MvpBasePresenter<RecordListView> {

    @Inject
    public RecordSearchPresenter() {}

    public void initView(Context context) {}

    protected Date getDate(String value) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        try {
            java.util.Date date = simpleDateFormat.parse(value);
            return new Date(date.getTime());
        } catch (ParseException e) {
        }

        return null;
    }
}
