package org.unicef.rapidreg.base;

import android.content.Context;
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

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.hannesdorfmann.mosby.mvp.MvpFragment;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.childcase.CaseActivity;
import org.unicef.rapidreg.childcase.CaseFeature;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.TracingFormService;
import org.unicef.rapidreg.tracing.TracingActivity;
import org.unicef.rapidreg.tracing.TracingFeature;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public abstract class RecordListFragment extends MvpFragment<RecordListView, RecordListPresenter>
        implements RecordListView {

    protected static final int HAVE_RESULT_LIST = 0;
    protected static final int HAVE_NO_RESULT = 1;

    @BindView(R.id.list_container)
    protected RecyclerView listContainer;

    @BindView(R.id.order_spinner)
    protected Spinner orderSpinner;

    @BindView(R.id.floating_menu)
    protected FloatingActionsMenu floatingMenu;

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
        initFloatingMenu();
    }

    @OnClick(R.id.add_case)
    public void onCaseAddClicked() {
        RecordService.clearAudioFile();
        if (!CaseFormService.getInstance().isFormReady()) {
            Toast.makeText(getActivity(), R.string.syncing_forms_text, Toast.LENGTH_LONG).show();
            return;
        }
        floatingMenu.collapseImmediately();

        CaseActivity activity = (CaseActivity) getActivity();
        activity.turnToFeature(CaseFeature.ADD, null);
    }

    @OnClick(R.id.add_tracing_request)
    public void onTracingAddClicked() {
        RecordService.clearAudioFile();
        if (!TracingFormService.getInstance().isFormReady()) {
            Toast.makeText(getActivity(), R.string.syncing_forms_text, Toast.LENGTH_LONG).show();
            return;
        }
        floatingMenu.collapseImmediately();

        TracingActivity activity = (TracingActivity) getActivity();
        activity.turnToFeature(TracingFeature.ADD, null);
    }

    public void toggleMode(boolean isShow) {
        adapter.toggleViews(isShow);
    }

    private void setListAlpha(float value) {
        container.setAlpha(value);
    }

    private void initFloatingMenu() {
        floatingMenu.setOnFloatingActionsMenuUpdateListener(
                new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
                    @Override
                    public void onMenuExpanded() {
                        setListAlpha(0.5f);
                    }

                    @Override
                    public void onMenuCollapsed() {
                        setListAlpha(1.0f);
                    }
                });
    }

    protected abstract void initOrderSpinner(final RecordListAdapter adapter);

    protected abstract void initListContainer(final RecordListAdapter adapter);

    public enum SpinnerState {
        AGE_ASC(R.drawable.age_up, "Age ascending order", "Age"),
        AGE_DES(R.drawable.age_down, "Age descending order", "Age"),
        DATE_ASC(R.drawable.date_up, "Registration date ascending order", "Registration date"),
        DATE_DES(R.drawable.date_down, "Registration date descending order", "Registration date");
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
