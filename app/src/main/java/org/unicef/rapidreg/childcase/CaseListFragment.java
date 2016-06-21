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
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
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

    @BindView(R.id.container)
    LinearLayout container;

    private CaseListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cases_list, container, false);
        FloatingActionsMenu floatingMenu
                = (FloatingActionsMenu) view.findViewById(R.id.floating_menu);
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
        return view;
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
        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(getActivity(),
                R.array.order_array, android.R.layout.simple_spinner_item);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orderSpinner.setAdapter(adapterSpinner);
        orderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CaseService caseService = CaseService.getInstance();
                if (position == 0) {
                    adapter.setCaseList(caseService.getCaseList());
                } else {
                    adapter.setCaseList(caseService.getCaseListOrderByAge());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @OnClick(R.id.add_case)
    public void onCaseAddClicked() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_content, new CaseRegisterWrapperFragment(),
                        CaseRegisterWrapperFragment.class.getSimpleName())
                .commit();
    }

    public void toggleMode(boolean isShow) {
        adapter.toggleViews(isShow);
    }

    private void setListAlpha(float value) {
        container.setAlpha(value);
    }
}
