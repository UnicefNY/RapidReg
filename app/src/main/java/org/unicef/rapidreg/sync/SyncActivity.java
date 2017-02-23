package org.unicef.rapidreg.sync;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.BaseActivity;
import org.unicef.rapidreg.base.Feature;

public class SyncActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_content, new SyncFragment(), null).commit();
        hideAllToolbarIcons();
        toolbarTitle.setText(R.string.sync);

        setNavSelectedMenu(R.id.nav_sync, syncColor);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    protected void navSyncAction() {

    }

    @Override
    protected void navCaseAction() {
        intentSender.showCasesActivity(this, true, false);
    }

    @Override
    protected void navTracingAction() {
        intentSender.showTracingActivity(this, true);
    }

    @Override
    protected void navIncidentAction() {
        intentSender.showIncidentActivity(this, true);
    }

    @Override
    protected void processBackButton() {
        intentSender.showCasesActivity(this, true, false);
    }

    @Override
    protected void search() {

    }

    @Override
    protected void save() {

    }

    @Override
    protected void showHideDetail() {

    }

    @Override
    public Feature getCurrentFeature() {
        return null;
    }
}
