package org.unicef.rapidreg.childcase;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.view.BaseActivity;

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
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_content, new CasesRegisterFragment())
                        .commit();
                return true;
            }
            return false;
        }
    }
}
