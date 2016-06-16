package org.unicef.rapidreg.childcase;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.view.BaseActivity;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.CaseService;

import static org.unicef.rapidreg.service.CaseService.CaseValues;

public class CaseActivity extends BaseActivity {
    public final static String INTENT_KEY_IS_IN_VIEW_MODE = "_is_in_view_mode";
    public final static String INTENT_KEY_CASE_INFO = "_case_info";


    public void setAddCaseVisible(boolean visible) {
        toolbar.getMenu().findItem(R.id.add_case).setVisible(visible);
    }

    public void setSaveCaseVisible(boolean visible) {
        toolbar.getMenu().findItem(R.id.save_case).setVisible(visible);
    }

    public void setEditCaseVisible(boolean visible) {
        toolbar.getMenu().findItem(R.id.edit_case).setVisible(visible);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar.inflateMenu(R.menu.toolbar_main);
        toolbar.setOnMenuItemClickListener(new CaseMenuItemListener());
        toolbar.setTitle("Cases");

        if (savedInstanceState == null) {
            redirectFragment(new CaseListFragment(), false);
            setAddCaseVisible(true);
            setSaveCaseVisible(false);
        }
    }

    private class CaseMenuItemListener implements Toolbar.OnMenuItemClickListener {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.search:
                    redirectFragment(new CaseSearchFragment(), true);
                    return true;
                case R.id.add_case:
                    return addCaseButtonAction();
                case R.id.save_case:
                    return saveCaseButtonAction();
                case R.id.edit_case:
                    return editCaseButtonAction();
                default:
                    return false;
            }
        }
    }

    private boolean saveCaseButtonAction() {
        CaseService.getInstance().saveOrUpdateCase(CaseValues.getValues());
        redirectFragment(new CaseListFragment(), false);

        setAddCaseVisible(true);
        setSaveCaseVisible(false);
        return true;
    }

    private boolean addCaseButtonAction() {
        CaseValues.clear();
        if (!CaseFormService.getInstance().isFormReady()) {
            Toast.makeText(CaseActivity.this,
                    R.string.syncing_forms_text, Toast.LENGTH_LONG).show();
            return true;
        }
        redirectFragment(new CaseRegisterWrapperFragment(), true);
        getIntent().removeExtra(INTENT_KEY_IS_IN_VIEW_MODE);
        setAddCaseVisible(false);
        setSaveCaseVisible(true);
        return true;
    }

    private boolean editCaseButtonAction() {
        redirectFragment(new CaseRegisterWrapperFragment(), false);
        getIntent().removeExtra(INTENT_KEY_IS_IN_VIEW_MODE);
        setSaveCaseVisible(true);
        setEditCaseVisible(false);
        return true;
    }

    private void redirectFragment(Fragment target, boolean needToBack) {
        String name = target.getClass().getSimpleName();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (needToBack) {
            transaction.replace(R.id.fragment_content, target, name)
                    .addToBackStack(null)
                    .commit();
        } else {
            transaction.replace(R.id.fragment_content, target, name)
                    .commit();
        }
        resetBarButtonsIfNeeded();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        resetBarButtonsIfNeeded();
    }

    private void resetBarButtonsIfNeeded() {
        if (isListFragmentVisible()) {
            setAddCaseVisible(true);
            setSaveCaseVisible(false);
        }
    }

    private boolean isListFragmentVisible() {
        Fragment listFragment = getSupportFragmentManager()
                .findFragmentByTag(CaseListFragment.class.getSimpleName());

        return listFragment != null && listFragment.isVisible();
    }
}
