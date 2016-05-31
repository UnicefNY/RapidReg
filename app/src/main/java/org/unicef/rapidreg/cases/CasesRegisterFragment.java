package org.unicef.rapidreg.cases;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import com.hannesdorfmann.mosby.mvp.MvpFragment;

import org.unicef.rapidreg.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CasesRegisterFragment extends MvpFragment<CasesRegisterView, CasesRegisterPresenter> implements CasesRegisterView {

    @BindView(R.id.fragment_register_content) LinearLayout registerContent;
    @BindView(R.id.register_forms_content) ExpandableListView formsContent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
    public CasesRegisterPresenter createPresenter() {
        return new CasesRegisterPresenter();
    }

    @Override
    public void initView(CasesRegisterAdapter adapter) {
        formsContent.setAdapter(adapter);
    }

    @Override
    public void expandAll(CasesRegisterAdapter adapter) {
        int count = adapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            formsContent.expandGroup(i);
        }
    }
}
