package org.unicef.rapidreg.base.record.recordlist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.hannesdorfmann.mosby.mvp.MvpFragment;

import org.greenrobot.eventbus.EventBus;
import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.PrimeroConfiguration;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.record.RecordActivity;
import org.unicef.rapidreg.event.LoadCaseFormEvent;
import org.unicef.rapidreg.event.LoadTracingFormEvent;
import org.unicef.rapidreg.injection.component.DaggerFragmentComponent;
import org.unicef.rapidreg.injection.component.FragmentComponent;
import org.unicef.rapidreg.injection.module.FragmentModule;
import org.unicef.rapidreg.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;

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

    @BindView(R.id.container)
    protected LinearLayout container;

    @BindView(R.id.list_result)
    protected ViewSwitcher viewSwitcher;

    protected RecordListAdapter adapter;

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
        adapter = createRecordListAdapter();

        initListContainer(adapter);
        initOrderSpinner(adapter);
    }

    public void toggleMode(boolean isShow) {
        adapter.toggleViews(isShow);
    }

    protected FragmentComponent getComponent() {
        return DaggerFragmentComponent.builder()
                .applicationComponent(PrimeroApplication.get(getContext()).getComponent())
                .fragmentModule(new FragmentModule(this))
                .build();
    }

    protected void showSyncFormDialog(String message) {
        RecordActivity activity = (RecordActivity) getActivity();
        if (!activity.isFormSyncFail()) {
            Toast.makeText(activity, R.string.forms_is_syncing_msg, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.sync_forms)
                .setMessage(String.format("%s %s", message, getResources().getString(R.string.sync_forms_message)))
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EventBus.getDefault().postSticky(new LoadCaseFormEvent(PrimeroConfiguration.getCookie()));
                        EventBus.getDefault().postSticky(new LoadTracingFormEvent(PrimeroConfiguration.getCookie()));
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
        Utils.changeDialogDividerColor(getActivity(), dialog);
    }

    protected abstract RecordListAdapter createRecordListAdapter();

    protected abstract void initListContainer(final RecordListAdapter adapter);

    protected abstract void initOrderSpinner(final RecordListAdapter adapter);
}
