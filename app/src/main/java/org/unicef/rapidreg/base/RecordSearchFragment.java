package org.unicef.rapidreg.base;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.hannesdorfmann.mosby.mvp.MvpFragment;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.widgets.ClearableEditText;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public abstract class RecordSearchFragment extends MvpFragment<RecordListView, RecordListPresenter>
        implements RecordListView {
    public static final String TAG = RecordSearchFragment.class.getSimpleName();

    protected static final String ID = "id";
    protected static final String NAME = "name";
    protected static final String AGE_FROM = "age_from";
    protected static final String AGE_TO = "age_to";
    protected static final String CAREGIVER = "caregiver";
    protected static final String REGISTRATION_DATE = "registration_date";

    private static final int HAVE_RESULT_LIST = 0;
    private static final int HAVE_NO_RESULT = 1;

    @BindView(R.id.list_container)
    protected RecyclerView listContainer;

    @BindView(R.id.view_switcher)
    protected ViewSwitcher viewSwitcher;

    @BindView(R.id.search_bar_title)
    protected TextView searchBarTitle;

    @BindView(R.id.id)
    protected ClearableEditText id;

    @BindView(R.id.name)
    protected ClearableEditText name;

    @BindView(R.id.age_from)
    protected ClearableEditText ageFrom;

    @BindView(R.id.age_to)
    protected ClearableEditText ageTo;

    @BindView(R.id.caregiver)
    protected ClearableEditText caregiver;

    @BindView(R.id.caregiver_separator)
    protected View caregiverSeparator;

    @BindView(R.id.registration_date)
    protected TextView registrationDate;

    @BindView(R.id.registration_date_clear)
    protected ImageButton registrationDateClear;

    @BindView(R.id.search_result)
    protected ViewSwitcher searchResultSwitcher;

    private RecordListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_records_search, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        presenter.initView(getActivity());
    }

    @Override
    public void initView(final RecordListAdapter adapter) {
        this.adapter = adapter;

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listContainer.setLayoutManager(layoutManager);
        listContainer.setAdapter(adapter);
    }

    @OnClick(R.id.search_bar)
    public void onSearchBarClicked() {
        viewSwitcher.showNext();
    }

    @OnClick(R.id.registration_date_container)
    public void onRegistrationDateContainerClicked() {
        final DatePicker datePicker = new DatePicker(getActivity());
        datePicker.setCalendarViewShown(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.registration_date));
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String date = String.format("%s/%s/%s", datePicker.getMonth() + 1,
                        datePicker.getDayOfMonth(), datePicker.getYear());
                registrationDate.setText(date);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.setView(datePicker);
        builder.create().show();
    }

    @OnTextChanged(R.id.registration_date)
    public void onRegistrationDateTextChanged(CharSequence s) {
        if (s.length() > 0) {
            registrationDateClear.setVisibility(View.VISIBLE);
        } else {
            registrationDateClear.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(R.id.registration_date_clear)
    public void onRegistrationDateClear() {
        registrationDate.setText("");
    }

    @OnClick(R.id.done)
    public void onDoneClicked() {
        viewSwitcher.showPrevious();
        Map<String, String> values = getFilterValues();
        searchBarTitle.setText(getFirstValidValue(values));

        List<? extends RecordModel> searchResult = getSearchResult(values);

        int resultIndex = searchResult.isEmpty() ? HAVE_NO_RESULT : HAVE_RESULT_LIST;
        searchResultSwitcher.setDisplayedChild(resultIndex);

        adapter.setRecordList(searchResult);
        adapter.notifyDataSetChanged();
    }

    protected Date getDate(String value) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        try {
            java.util.Date date = simpleDateFormat.parse(value);
            return new Date(date.getTime());
        } catch (ParseException e) {
        }

        return null;
    }

    private Map<String, String> getFilterValues() {
        Map<String, String> values = new LinkedHashMap<>();
        values.put(ID, id.getText());
        values.put(NAME, name.getText());
        values.put(AGE_FROM, ageFrom.getText());
        values.put(AGE_TO, ageTo.getText());
        values.put(CAREGIVER, caregiver.getText());
        values.put(REGISTRATION_DATE, registrationDate.getText().toString());

        return values;
    }

    private String getFirstValidValue(Map<String, String> values) {
        for (String key : values.keySet()) {
            String value = values.get(key);
            if (!TextUtils.isEmpty(value)) {
                return value;
            }
        }

        return getResources().getString(R.string.click_to_search);
    }

    protected abstract List<? extends RecordModel> getSearchResult(Map<String, String> filters);
}


