package org.unicef.rapidreg.childcase;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.hannesdorfmann.mosby.mvp.MvpFragment;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.widgets.ClearableEditText;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CaseSearchFragment extends MvpFragment<CaseListView, CaseListPresenter>
        implements CaseListView {

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String AGE_FROM = "age_from";
    private static final String AGE_TO = "age_to";
    private static final String CAREGIVER = "caregiver";

    @BindView(R.id.list_container)
    RecyclerView caseListContainer;

    @BindView(R.id.view_switcher)
    ViewSwitcher viewSwitcher;

    @BindView(R.id.search_bar_title)
    TextView searchBarTitle;

    @BindView(R.id.id)
    ClearableEditText id;

    @BindView(R.id.name)
    ClearableEditText name;

    @BindView(R.id.age_from)
    ClearableEditText ageFrom;

    @BindView(R.id.age_to)
    ClearableEditText ageTo;

    @BindView(R.id.caregiver)
    ClearableEditText caregiver;

    private CaseListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cases_search, container, false);
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
    }

    @OnClick(R.id.search_bar)
    public void onSearchBarClicked() {
        viewSwitcher.showNext();
    }

    @OnClick(R.id.done)
    public void onDoneClicked() {
        viewSwitcher.showPrevious();
        Map<String, String> values = getFilterValues();

        searchBarTitle.setText(getFirstValidValue(values));
        List<Case> searchResult;
        searchResult = getSearchResult(values);
        adapter.setCaseList(searchResult);
        adapter.notifyDataSetChanged();
    }

    private Map<String, String> getFilterValues() {
        Map<String, String> values = new LinkedHashMap<>();
        values.put(ID, id.getText());
        values.put(NAME, name.getText());
        values.put(AGE_FROM, ageFrom.getText());
        values.put(AGE_TO, ageTo.getText());
        values.put(CAREGIVER, caregiver.getText());

        return values;
    }

    private String getFirstValidValue(Map<String, String> values) {
        for (String key : values.keySet()) {
            String value = values.get(key);
            if (!TextUtils.isEmpty(value)) {
                return value;
            }
        }

        return getResources().getString(R.string.click_to_search);
    }

    private List<Case> getSearchResult(Map<String, String> filters) {
        String id = filters.get(ID);
        String name = filters.get(NAME);
        String from = filters.get(AGE_FROM);
        int ageFrom = TextUtils.isEmpty(from) ? Case.MIN_AGE : Integer.valueOf(from);
        String to = filters.get(AGE_TO);
        int ageTo = TextUtils.isEmpty(to) ? Case.MAX_AGE : Integer.valueOf(to);
        String caregiver = filters.get(CAREGIVER);

        return CaseService.getInstance().getSearchResult(id, name, ageFrom, ageTo, caregiver, "");
    }
}
