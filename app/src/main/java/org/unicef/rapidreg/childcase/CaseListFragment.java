package org.unicef.rapidreg.childcase;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hannesdorfmann.mosby.mvp.MvpFragment;

import org.unicef.rapidreg.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CaseListFragment extends MvpFragment<CaseListView, CaseListPresenter>
        implements CaseListView {

    @BindView(R.id.list_container)
    RecyclerView caseListContainer;

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
        presenter.initView();
    }

    @Override
    public CaseListPresenter createPresenter() {
        return new CaseListPresenter();
    }

    @Override
    public void initView(CaseListAdapter adapter) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        caseListContainer.setLayoutManager(layoutManager);
        caseListContainer.setAdapter(adapter);
    }
}
