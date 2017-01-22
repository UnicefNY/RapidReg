package org.unicef.rapidreg.base.record.recordsearch;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.hannesdorfmann.mosby.mvp.MvpFragment;

import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.BaseAlertDialog;
import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.base.record.recordlist.RecordListAdapter;
import org.unicef.rapidreg.base.record.recordlist.RecordListView;
import org.unicef.rapidreg.injection.component.DaggerFragmentComponent;
import org.unicef.rapidreg.injection.component.FragmentComponent;
import org.unicef.rapidreg.injection.module.FragmentModule;
import org.unicef.rapidreg.model.RecordModel;
import org.unicef.rapidreg.widgets.ClearableEditText;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

import static org.unicef.rapidreg.base.record.recordsearch.RecordSearchPresenter.*;
import static org.unicef.rapidreg.model.RecordModel.EMPTY_AGE;

public abstract class RecordSearchFragment extends MvpFragment<RecordListView, RecordSearchPresenter>
        implements RecordListView {
    public static final String TAG = RecordSearchFragment.class.getSimpleName();

    private static final int HAVE_RESULT_LIST = 0;
    private static final int HAVE_NO_RESULT = 1;

    @BindView(R.id.list_container)
    protected RecyclerView listContainer;

    @BindView(R.id.view_switcher)
    protected ViewSwitcher viewSwitcher;

    @BindView(R.id.search_bar_title)
    protected TextView searchBarTitle;

    @BindView(R.id.id_field)
    protected LinearLayout idField;
    @BindView(R.id.id)
    protected ClearableEditText id;

    @BindView(R.id.name_field)
    protected LinearLayout nameField;
    @BindView(R.id.name)
    protected ClearableEditText name;

    @BindView(R.id.age_field)
    protected LinearLayout ageField;
    @BindView(R.id.age_from)
    protected ClearableEditText ageFrom;
    @BindView(R.id.age_to)
    protected ClearableEditText ageTo;

    @BindView(R.id.caregiver_field)
    protected LinearLayout caregiverField;
    @BindView(R.id.caregiver)
    protected ClearableEditText caregiver;
    @BindView(R.id.caregiver_separator)
    protected View caregiverSeparator;

    @BindView(R.id.registration_date_field)
    protected LinearLayout registrationDateField;
    @BindView(R.id.registration_date)
    protected TextView registrationDate;
    @BindView(R.id.registration_date_clear)
    protected ImageButton registrationDateClear;

    @BindView(R.id.location_field)
    protected LinearLayout locationField;
    @BindView(R.id.location)
    protected ClearableEditText location;

    @BindView(R.id.search_result)
    protected ViewSwitcher searchResultSwitcher;

    @BindView(R.id.date_of_inquiry_field)
    protected LinearLayout dateOfInquiryField;
    @BindView(R.id.date_of_inquiry)
    protected TextView dateOfInquiry;
    @BindView(R.id.date_of_inquiry_clear)
    protected ImageButton dateOfInquiryClear;

    @BindView(R.id.survivor_code_field)
    protected LinearLayout survivorCodeField;
    @BindView(R.id.survivor_code)
    protected ClearableEditText survivorCode;

    @BindView(R.id.type_of_violence_field)
    protected LinearLayout typeOfViolenceField;
    @BindView(R.id.type_of_violence)
    protected Spinner typeOfViolence;

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
        onInitViewContent();
        onInitSearchFields();
        ((RecordActivity)getActivity()).setShowHideSwitcherToShowState();
    }

    @Override
    public void onInitViewContent() {
        adapter = createRecordListAdapter();

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

        BaseAlertDialog.Builder builder = new BaseAlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.date));
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

    @OnClick(R.id.date_of_inquiry_container)
    public void onDateOfInquiryContainerClicked() {
        final DatePicker datePicker = new DatePicker(getActivity());
        datePicker.setCalendarViewShown(false);

        BaseAlertDialog.Builder builder = new BaseAlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.date));
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

    @OnTextChanged(R.id.date_of_inquiry)
    public void onDateOfInquiryTextChanged(CharSequence s) {
        if (s.length() > 0) {
            registrationDateClear.setVisibility(View.VISIBLE);
        } else {
            registrationDateClear.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(R.id.date_of_inquiry_clear)
    public void onDateOfInquiryClear() {
        registrationDate.setText("");
    }

    @OnClick(R.id.done)
    public void onDoneClicked() {
        viewSwitcher.showPrevious();
        Map<String, String> values = getFilterValues();
        searchBarTitle.setText(getFirstValidValue(values));

        String from = ageFrom.getText();
        String to = ageTo.getText();

        List<Long> searchResult = presenter.getSearchResult(id.getText(),
                name.getText(),
                TextUtils.isEmpty(from) ? RecordModel.EMPTY_AGE : Integer.valueOf(from),
                TextUtils.isEmpty(to) ? RecordModel.EMPTY_AGE : Integer.valueOf(to),
                caregiver.getText(),
                registrationDate.getText().toString());

        int resultIndex = searchResult.isEmpty() ? HAVE_NO_RESULT : HAVE_RESULT_LIST;
        searchResultSwitcher.setDisplayedChild(resultIndex);

        adapter.setRecordList(searchResult);
        adapter.notifyDataSetChanged();
    }

    public FragmentComponent getComponent() {
        return DaggerFragmentComponent.builder()
                .applicationComponent(PrimeroApplication.get(getContext()).getComponent())
                .fragmentModule(new FragmentModule(this))
                .build();
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

    protected abstract void onInitSearchFields();
    protected abstract RecordListAdapter createRecordListAdapter();
}


