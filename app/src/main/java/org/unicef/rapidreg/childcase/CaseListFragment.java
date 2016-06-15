package org.unicef.rapidreg.childcase;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.hannesdorfmann.mosby.mvp.MvpFragment;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.service.CaseService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CaseListFragment extends MvpFragment<CaseListView, CaseListPresenter>
        implements CaseListView {

    @BindView(R.id.list_container)
    RecyclerView caseListContainer;


    @BindView(R.id.order_spinner)
    Spinner orderSpinner;

    @BindView(R.id.toggle)
    ImageButton toggle;

    private CaseListAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cases_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        presenter.initView(getActivity());
    }

    @Override
    public CaseListPresenter createPresenter() {
        return new CaseListPresenter();
    }

    @Override
    public void initView(final CaseListAdapter adapter) {
        this.adapter = adapter;

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        caseListContainer.setLayoutManager(layoutManager);
        caseListContainer.setAdapter(adapter);
        ArrayAdapter<CharSequence> adapterSnpinner = ArrayAdapter.createFromResource(getActivity(),
                R.array.order_array, android.R.layout.simple_spinner_item);
        adapterSnpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orderSpinner.setAdapter(adapterSnpinner);
        orderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CaseService caseService = CaseService.getInstance();
                if (position == 0) {
                    adapter.setCaseList(caseService.getCaseList());
                } else {
                    adapter.setCaseList(caseService.getCaseListOrderByAge());
                }
                ;
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        hideToggleIfNeeded();
    }

    @OnClick(R.id.toggle)
    public void onToggleClicked() {
        if (hideToggleIfNeeded()) {
            return;
        }

        DetailState nextState = getNextToggleState();
        setToggle(nextState);
        adapter.toggleViews(nextState.isDetailShow());
    }

    private boolean hideToggleIfNeeded() {
        if (adapter == null || adapter.isListEmpty()) {
            toggle.setVisibility(View.INVISIBLE);
            return true;
        }

        return false;
    }

    private DetailState getNextToggleState() {
        DetailState currentState = DetailState.valueOf((String) toggle.getTag());
        return currentState.getNextState();
    }

    private void setToggle(DetailState state) {
        toggle.setBackgroundResource(state.getResId());
        toggle.setTag(state.name());
    }

    public enum DetailState {
        VISIBILITY(R.drawable.ic_visibility, true),
        INVISIBILITY(R.drawable.ic_invisibility, false);

        private final int resId;
        private final boolean isDetailShow;

        DetailState(int resId, boolean isDetailShow) {
            this.resId = resId;
            this.isDetailShow = isDetailShow;
        }

        public DetailState getNextState() {
            return this == VISIBILITY ? INVISIBILITY : VISIBILITY;
        }

        public int getResId() {
            return resId;
        }

        public boolean isDetailShow() {
            return isDetailShow;
        }
    }
}
