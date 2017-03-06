package org.unicef.rapidreg.base.record.recordsearch;

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
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.hannesdorfmann.mosby.mvp.MvpFragment;

import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.base.record.recordlist.RecordListAdapter;
import org.unicef.rapidreg.base.record.recordlist.RecordListView;
import org.unicef.rapidreg.injection.component.DaggerFragmentComponent;
import org.unicef.rapidreg.injection.component.FragmentComponent;
import org.unicef.rapidreg.injection.module.FragmentModule;
import org.unicef.rapidreg.widgets.ClearableEditText;
import org.unicef.rapidreg.widgets.dialog.MessageDialog;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

import static org.unicef.rapidreg.base.record.recordsearch.RecordSearchPresenter.CONSTANT.AGE_FROM;
import static org.unicef.rapidreg.base.record.recordsearch.RecordSearchPresenter.CONSTANT.AGE_TO;
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
    protected ClearableEditText typeOfViolence;

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
        ((RecordActivity) getActivity()).setShowHideSwitcherToShowState();
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
        showDatePickerDialog(registrationDate);
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
        showDatePickerDialog(dateOfInquiry);
    }

    @OnTextChanged(R.id.date_of_inquiry)
    public void onDateOfInquiryTextChanged(CharSequence s) {
        if (s.length() > 0) {
            dateOfInquiryClear.setVisibility(View.VISIBLE);
        } else {
            dateOfInquiryClear.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(R.id.date_of_inquiry_clear)
    public void onDateOfInquiryClear() {
        dateOfInquiry.setText("");
    }

    @OnClick(R.id.done)
    public void onDoneClicked() {
        viewSwitcher.showPrevious();
        Map<String, String> values = getFilterValues();
        searchBarTitle.setText(getFirstValidValue(values));

        List<Long> searchResult = presenter.getSearchResult(values);

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

    private String getFirstValidValue(Map<String, String> values) {
        for (String key : values.keySet()) {
            String value = values.get(key);
            if (!TextUtils.isEmpty(value)) {
                if (AGE_FROM.equals(key) || AGE_TO.equals(key)) {
                    if (EMPTY_AGE == Integer.valueOf(value)) {
                        continue;
                    }
                }
                return value;
            }
        }

        return getResources().getString(R.string.click_to_search);
    }

    private void showDatePickerDialog(TextView textView) {
        MessageDialog messageDialog = new MessageDialog(getActivity());
        messageDialog.setTitle(R.string.date);
        DatePicker datePicker = messageDialog.getDatePicker();
        datePicker.setCalendarViewShown(false);
        messageDialog.setPositiveButton(R.string.ok, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = String.format("%s/%s/%s", datePicker.getMonth() + 1,
                        datePicker.getDayOfMonth(), datePicker.getYear());
                textView.setText(date);
                messageDialog.dismiss();
            }
        });
        messageDialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageDialog.dismiss();
            }
        });
        messageDialog.show();
    }

    protected abstract Map<String, String> getFilterValues();

    protected abstract void onInitSearchFields();

    protected abstract RecordListAdapter createRecordListAdapter();
}


