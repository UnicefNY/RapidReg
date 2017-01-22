package org.unicef.rapidreg.base.record.recordsearch;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import org.unicef.rapidreg.base.record.recordlist.RecordListView;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class RecordSearchPresenter extends MvpBasePresenter<RecordListView> {

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String AGE_FROM = "age_from";
    public static final String AGE_TO = "age_to";
    public static final String CAREGIVER = "caregiver";
    public static final String REGISTRATION_DATE = "registration_date";
    public static final String DATE_OF_INQUIRY = "date_of_inquiry";
    public static final String LOCATION = "location";
    public static final String SURVIVOR_CODE = "survivor_code";
    public static final String TYPE_OF_VIOLENCE = "type_of_violence";

    protected Date getDate(String value) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        try {
            java.util.Date date = simpleDateFormat.parse(value);
            return new Date(date.getTime());
        } catch (ParseException e) {}

        return null;
    }

    protected abstract List<Long> getSearchResult(Map<String, String> searchConditions);
}
