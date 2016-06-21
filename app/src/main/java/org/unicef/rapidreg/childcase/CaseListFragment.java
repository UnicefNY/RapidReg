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
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.hannesdorfmann.mosby.mvp.MvpFragment;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.service.CaseService;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CaseListFragment extends MvpFragment<CaseListView, CaseListPresenter>
        implements CaseListView {

    @BindView(R.id.list_container)
    RecyclerView caseListContainer;


    @BindView(R.id.order_spinner)
    Spinner orderSpinner;

    @BindView(R.id.header_bar)
    RelativeLayout layout;

    private CaseListAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View caseListView = inflater.inflate(R.layout.fragment_cases_list, container, false);

        View addTRBtn = caseListView.findViewById(R.id.add_tracing_request);
        View addCaseBtn = caseListView.findViewById(R.id.add_case);

        addCaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_content, new CaseRegisterWrapperFragment(), null)
                        .commit();
            }
        });
        return caseListView;
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

        hideHeaderIfNeeded();
    }

    public void toggleMode(boolean isShow) {
        adapter.toggleViews(isShow);
    }

    private boolean hideHeaderIfNeeded() {
        if (adapter == null || adapter.isListEmpty()) {
            layout.setVisibility(View.GONE);
            return true;
        }

        return false;
    }
}
