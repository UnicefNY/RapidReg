package org.unicef.rapidreg.base.record.recordlist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.hannesdorfmann.mosby.mvp.MvpFragment;

import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.base.record.recordlist.spinner.SpinnerAdapter;
import org.unicef.rapidreg.base.record.recordlist.spinner.SpinnerState;
import org.unicef.rapidreg.injection.component.DaggerFragmentComponent;
import org.unicef.rapidreg.injection.component.FragmentComponent;
import org.unicef.rapidreg.injection.module.FragmentModule;
import org.unicef.rapidreg.widgets.dialog.MessageDialog;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public abstract class RecordListFragment extends MvpFragment<RecordListView, RecordListPresenter>
        implements RecordListView, RecordListAdapter.OnViewUpdateListener {

    public static final int HAVE_RESULT_LIST = 0;
    public static final int HAVE_NO_RESULT = 1;
    public static final int ANIMATION_DURATION = 2000;

    @BindView(R.id.list_container)
    protected RecyclerView listContainer;

    @BindView(R.id.order_spinner)
    protected Spinner orderSpinner;

    @BindView(R.id.add)
    protected FloatingActionButton addButton;

    @BindView(R.id.container)
    protected LinearLayout container;

    @BindView(R.id.list_result)
    protected ViewSwitcher viewSwitcher;

    @BindView(R.id.list_delete_button_content)
    LinearLayout listDeleteBtnContent;

    @BindView(R.id.list_item_delete_button)
    Button listItemDeleteBtn;

    @BindView(R.id.list_item_delete_button_gray)
    Button unclickbleListItemDeleteBtn;

    @BindView(R.id.list_item_delete_cancel_button)
    Button listItemDeleteCancelBtn;

    protected RecordListAdapter recordListAdapter;
    protected LinearLayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_records_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        onInitViewContent();
    }

    @Override
    public void onInitViewContent() {
        recordListAdapter = createRecordListAdapter();
        recordListAdapter.setOnViewUpdateListener(this);
        initListContainer(recordListAdapter);
        initOrderSpinner(recordListAdapter);
    }

    protected FragmentComponent getComponent() {
        return DaggerFragmentComponent.builder()
                .applicationComponent(PrimeroApplication.get(getContext()).getComponent())
                .fragmentModule(new FragmentModule(this))
                .build();
    }

    public void toggleMode(boolean isShow) {
        recordListAdapter.toggleViews(isShow);
    }

    public void toggleDeleteMode(boolean isDeleteMode) {
        if (isDeleteMode) {
            listDeleteBtnContent.setVisibility(View.VISIBLE);
            addButton.setVisibility(View.GONE);
        } else {
            listDeleteBtnContent.setVisibility(View.GONE);
            addButton.setVisibility(View.VISIBLE);
            ((RecordActivity) getActivity()).showListMode();
        }
        recordListAdapter.toggleDeleteViews(isDeleteMode);
    }

    public void showSyncFormDialog(String message) {
        MessageDialog messageDialog = new MessageDialog(getActivity());
        messageDialog.setTitle(R.string.sync_forms);
        messageDialog.setMessage(String.format("%s %s", message, getResources().getString(R.string
                .sync_forms_message)));
        messageDialog.setPositiveButton(R.string.ok, v -> {
            ((RecordActivity) getActivity()).sendSyncFormEvent();
            messageDialog.dismiss();
        });
        messageDialog.setNegativeButton(R.string.cancel, v -> {
            messageDialog.dismiss();
        });
        messageDialog.show();
    }

    private void initListContainer(final RecordListAdapter adapter) {
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(ANIMATION_DURATION);
        itemAnimator.setRemoveDuration(ANIMATION_DURATION);
        listContainer.setLayoutManager(layoutManager);
        listContainer.setItemAnimator(itemAnimator);
        listContainer.setAdapter(adapter);
        viewSwitcher.setDisplayedChild(presenter.calculateDisplayedIndex());
    }

    private void initOrderSpinner(final RecordListAdapter adapter) {
        final SpinnerState[] spinnerStates = getDefaultSpinnerStates();
        int defaultSpinnerStatePosition = getDefaultSpinnerStatePosition();
        orderSpinner.setAdapter(new SpinnerAdapter(getActivity(),
                R.layout.record_list_spinner_opened, Arrays.asList(spinnerStates)));
        orderSpinner.setSelection(defaultSpinnerStatePosition);
        orderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<Long> filterRecords = presenter.getRecordsByFilter(spinnerStates[position]);
                if (filterRecords == null || filterRecords.isEmpty()) {
                    return;
                }
                adapter.setRecordList(filterRecords);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @OnClick(R.id.list_item_delete_button)
    public void onItemDeleteButtonClick() {
        MessageDialog messageDialog = new MessageDialog(getActivity());
        messageDialog.setTitle(R.string.delete_title);
        messageDialog.setMessage(getResources().getString(R.string.delete_confirm_message));
        messageDialog.setPositiveButton(R.string.yes, view -> {
            listContainer.scrollToPosition(recordListAdapter.caculateRetainedPosition());
            recordListAdapter.removeRecords();
            toggleDeleteMode(false);
            messageDialog.dismiss();
            Toast.makeText(getActivity(), R.string.delete_success_info, Toast.LENGTH_SHORT).show();
        });
        messageDialog.setNegativeButton(R.string.no, view -> messageDialog.dismiss());
        messageDialog.show();
    }

    @OnClick(R.id.list_item_delete_cancel_button)
    public void onItemDeleteCancelButtonClick() {
        int retainedPosition = layoutManager.findFirstVisibleItemPosition();
        listContainer.scrollToPosition(retainedPosition);
        toggleDeleteMode(false);
    }

    @Override
    public void onRecordsDeletable(boolean isDeletable) {
        if (isDeletable) {
            listItemDeleteBtn.setVisibility(View.VISIBLE);
            unclickbleListItemDeleteBtn.setVisibility(View.GONE);
        } else {
            unclickbleListItemDeleteBtn.setVisibility(View.VISIBLE);
            listItemDeleteBtn.setVisibility(View.GONE);
        }
    }

    protected abstract RecordListAdapter createRecordListAdapter();

    protected abstract int getDefaultSpinnerStatePosition();

    protected abstract SpinnerState[] getDefaultSpinnerStates();
}
