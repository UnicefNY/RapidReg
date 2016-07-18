package org.unicef.rapidreg.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.hannesdorfmann.mosby.mvp.MvpFragment;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.service.cache.ItemValues;
import org.unicef.rapidreg.service.cache.ItemValuesMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class RecordRegisterFragment extends MvpFragment<RecordRegisterView, RecordRegisterPresenter>
        implements RecordRegisterView {

    public static final String ITEM_VALUES = "item_values";

    @BindView(R.id.register_forms_content)
    RecyclerView fieldList;

    protected RecordPhotoAdapter photoAdapter;
    protected ItemValuesMap itemValues;

    private RecordRegisterAdapter recordRegisterAdapter;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        int position = FragmentPagerItem.getPosition(getArguments());
        presenter.initContext(getActivity(), position);
    }

    @Override
    public void initView(RecordRegisterAdapter adapter) {
        recordRegisterAdapter = adapter;
        RecyclerView.LayoutManager layout = new LinearLayoutManager(getContext());
        layout.setAutoMeasureEnabled(true);
        fieldList.setLayoutManager(layout);
        fieldList.setAdapter(recordRegisterAdapter);
        recordRegisterAdapter.setPhotoAdapter(photoAdapter);
        recordRegisterAdapter.setItemValues(itemValues);
    }

    public RecordRegisterAdapter getRegisterAdapter() {
        return recordRegisterAdapter;
    }

    public void clearFocus() {
        View focusedChild = fieldList.getFocusedChild();
        if (focusedChild != null) {
            focusedChild.clearFocus();
        }
    }
}
