package org.unicef.rapidreg.childcase;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.hannesdorfmann.mosby.mvp.MvpFragment;

import org.unicef.rapidreg.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CaseMiniRegisterFragment extends MvpFragment<CaseMiniRegisterView, CaseMiniRegisterPresenter>
        implements CaseMiniRegisterView{

    @BindView(R.id.register_forms_content)
    ListView fieldList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_cases_register, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        presenter.initContext(getActivity());
    }

    @Override
    public CaseMiniRegisterPresenter createPresenter() {
        return new CaseMiniRegisterPresenter();
    }

    @Override
    public void initView(CaseMiniRegisterAdapter adapter) {
        fieldList.setAdapter(adapter);
    }
}
