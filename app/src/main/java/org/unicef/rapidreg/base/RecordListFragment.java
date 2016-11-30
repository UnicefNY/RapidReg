package org.unicef.rapidreg.base;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.hannesdorfmann.mosby.mvp.MvpFragment;

import org.greenrobot.eventbus.EventBus;
import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.PrimeroConfiguration;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.event.LoadCaseFormEvent;
import org.unicef.rapidreg.event.LoadTracingFormEvent;
import org.unicef.rapidreg.injection.component.DaggerFragmentComponent;
import org.unicef.rapidreg.injection.component.FragmentComponent;
import org.unicef.rapidreg.injection.module.FragmentModule;
import org.unicef.rapidreg.utils.Utils;

import java.util.List;

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
        presenter.initView(getActivity());
    }

    @Override
    public void initView(final RecordListAdapter adapter) {
        this.adapter = adapter;

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

    protected abstract void initListContainer(final RecordListAdapter adapter);

    protected abstract void initOrderSpinner(final RecordListAdapter adapter);

    public enum SpinnerState {
        AGE_ASC(R.drawable.age_up, "Age ascending order", "Age"),
        AGE_DES(R.drawable.age_down, "Age descending order", "Age"),
        REG_DATE_ASC(R.drawable.date_up, "Registration date ascending order", "Registration date"),
        REG_DATE_DES(R.drawable.date_down, "Registration date descending order", "Registration date"),
        INQUIRY_DATE_ASC(R.drawable.date_up, "Date of inquiry ascending order", "Date of inquiry"),
        INQUIRY_DATE_DES(R.drawable.date_down, "Date of inquiry descending order", "Date of inquiry");

        private int resId;
        private String longName;

        private String shortName;

        SpinnerState(int resId, String longName, String shortName) {
            this.resId = resId;
            this.longName = longName;
            this.shortName = shortName;
        }

        public int getResId() {
            return resId;
        }

        public String getLongName() {
            return longName;
        }

        public String getShortName() {
            return shortName;
        }
    }

    public class SpinnerAdapter extends ArrayAdapter<SpinnerState> {

        private final List<SpinnerState> states;

        public SpinnerAdapter(Context context, int resource, List<SpinnerState> states) {
            super(context, resource, states);
            this.states = states;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, parent, true);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, parent, false);
        }

        public View getCustomView(int position, ViewGroup parent, boolean isDropDownView) {
            SpinnerState state = states.get(position);

            LayoutInflater inflater = LayoutInflater.from(getActivity());
            int layoutId = isDropDownView ? R.layout.record_list_spinner_opened : R.layout.record_list_spinner_closed;
            View view = inflater.inflate(layoutId, parent, false);

            ImageView indicator = (ImageView) view.findViewById(R.id.indicator);
            TextView orderName = (TextView) view.findViewById(R.id.order_name);

            indicator.setImageResource(state.getResId());
            orderName.setText(isDropDownView ? state.getLongName() : state.getShortName());

            return view;
        }
    }
}
