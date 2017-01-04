package org.unicef.rapidreg.base.record.recordlist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.hannesdorfmann.mosby.mvp.MvpFragment;
import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.BaseAlertDialog;
import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.base.record.recordlist.spinner.SpinnerAdapter;
import org.unicef.rapidreg.base.record.recordlist.spinner.SpinnerState;
import org.unicef.rapidreg.base.record.recordregister.RecordRegisterBtnType;
import org.unicef.rapidreg.injection.component.DaggerFragmentComponent;
import org.unicef.rapidreg.injection.component.FragmentComponent;
import org.unicef.rapidreg.injection.module.FragmentModule;
import org.unicef.rapidreg.utils.Utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.*;
import static android.view.View.INVISIBLE;

public abstract class RecordListFragment extends MvpFragment<RecordListView, RecordListPresenter>
        implements RecordListView {

    public static final int HAVE_RESULT_LIST = 0;
    public static final int HAVE_NO_RESULT = 1;

    @BindView(R.id.list_container)
    protected RecyclerView listContainer;

    @BindView(R.id.order_spinner)
    protected Spinner orderSpinner;

    @BindView(R.id.add)
    protected FloatingActionButton addButton;

    @BindView(R.id.add_menu)
    protected FloatingActionsMenu addMenu;

    @BindView(R.id.container)
    protected LinearLayout container;

    @BindView(R.id.list_result)
    protected ViewSwitcher viewSwitcher;

    protected RecordListAdapter recordListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container,
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

        initListContainer(recordListAdapter);
        initOrderSpinner(recordListAdapter);

        initCreateEventView(getCreateEvents());
    }

    private void initCreateEventView(HashMap<RecordRegisterBtnType, OnClickListener> events) {
        if (events == null || events.size() <= 1) {
            initFloatingActionButton();
        } else {
            initFloatingActionMenu(events);
        }
    }

    private void initFloatingActionMenu(HashMap<RecordRegisterBtnType, OnClickListener> events) {
        addMenu.setVisibility(VISIBLE);
        addButton.setVisibility(INVISIBLE);
        for(Entry<RecordRegisterBtnType, OnClickListener> entry : events.entrySet()) {
            addMenu.addButton(createFloatingActionButton(entry.getKey(), entry.getValue()));
        }
    }

    private void initFloatingActionButton() {
        addButton.setVisibility(VISIBLE);
        addMenu.setVisibility(INVISIBLE);
    }

    private FloatingActionButton createFloatingActionButton(RecordRegisterBtnType type, OnClickListener listener) {
        FloatingActionButton button = new FloatingActionButton(getActivity());
        button.setTitle(type.getBtnTitle());
        button.setIcon(type.getResId());
        button.setColorNormalResId(R.color.primero_green);
        button.setColorPressedResId(R.color.primero_green);
        button.setOnClickListener(listener);

        return button;
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

    protected void showSyncFormDialog(String message) {
        RecordActivity activity = (RecordActivity) getActivity();
        if (activity.isFormSyncFail()) {
            Toast.makeText(activity, R.string.forms_is_syncing_msg, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        AlertDialog dialog = new BaseAlertDialog.Builder(getActivity())
                .setTitle(R.string.sync_forms)
                .setMessage(String.format("%s %s", message, getResources().getString(R.string
                        .sync_forms_message)))
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendSyncFormEvent();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
        Utils.changeDialogDividerColor(getActivity(), dialog);
    }

    private void initListContainer(final RecordListAdapter adapter) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listContainer.setLayoutManager(layoutManager);
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

    protected HashMap<RecordRegisterBtnType, OnClickListener> getCreateEvents() {
        return null;
    }

    protected abstract RecordListAdapter createRecordListAdapter();

    protected abstract int getDefaultSpinnerStatePosition();

    protected abstract SpinnerState[] getDefaultSpinnerStates();

    protected abstract void sendSyncFormEvent();
}
