package org.unicef.rapidreg.cases;

import android.os.Bundle;
import android.support.annotation.Nullable;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.view.BaseActivity;

public class CasesActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_content, new CasesListFragment())
                    .commit();
        }
    }
}
