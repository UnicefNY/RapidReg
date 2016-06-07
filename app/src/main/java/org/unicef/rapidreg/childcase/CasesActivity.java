package org.unicef.rapidreg.childcase;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.view.BaseActivity;
import org.unicef.rapidreg.forms.childcase.CaseFormRoot;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.CaseService;

public class CasesActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar.inflateMenu(R.menu.toolbar_main);
        toolbar.setOnMenuItemClickListener(new CaseMenuItemListener());

        toolbar.setTitle("Cases");

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

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_content, new CasesRegisterFragment())
                        .commit();
                return true;
            }
            return false;
        }
    }
}