package org.unicef.rapidreg.base.record.recordsearch;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import org.unicef.rapidreg.base.record.recordlist.RecordListView;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public abstract class RecordSearchPresenter extends MvpBasePresenter<RecordListView> {

    protected Date getDate(String value) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        try {
            java.util.Date date = simpleDateFormat.parse(value);
            return new Date(date.getTime());
        } catch (ParseException e) {}

        return null;
    }

    protected abstract List<Long> getSearchResult(String shortId,
                                                  String name,
                                                  int ageFrom,
                                                  int ageTo,
                                                  String caregiver,
                                                  String registrationDate);
}
