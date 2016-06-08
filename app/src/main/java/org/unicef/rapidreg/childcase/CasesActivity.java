package org.unicef.rapidreg.childcase;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.view.BaseActivity;
import org.unicef.rapidreg.service.CaseFormService;

public class CasesActivity extends BaseActivity {
    private static final String CASE_REGISTRATION = "Case_Registration";
    private Menu menu;
    private CasesRegisterFragment casesRegisterFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar.inflateMenu(R.menu.toolbar_main);
        toolbar.setOnMenuItemClickListener(new CaseMenuItemListener());

        toolbar.setTitle("Cases");
        menu = toolbar.getMenu();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_content, new CasesListFragment())
                    .commit();
        }
    }

    private class CaseMenuItemListener implements Toolbar.OnMenuItemClickListener {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            if (R.id.search == menuItem.getItemId()) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_content, new CasesSearchFragment())
                        .commit();
                return true;
            }
            if (R.id.add_case == menuItem.getItemId()) {
                if (!CaseFormService.getInstance().isFormReady()) {
                    Toast.makeText(CasesActivity.this,
                            R.string.syncing_forms_text, Toast.LENGTH_LONG).show();
                    return true;
                }

                Fragment caseFragment = getSupportFragmentManager().findFragmentByTag(CASE_REGISTRATION);
                if (caseFragment == null || !caseFragment.isVisible()) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_content, new CasesFragment(), CASE_REGISTRATION)
                            .commit();
                }
                menu.getItem(0).setVisible(false);
                menu.getItem(1).setVisible(true);
                return true;
            }

            if (R.id.save_case == menuItem.getItemId()) {
                menu.getItem(0).setVisible(true);
                menu.getItem(1).setVisible(false);
                saveCase();
                return true;
            }
            return false;
        }
    }

    private void saveCase() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_content, new CasesListFragment())
                .commit();
    }
}