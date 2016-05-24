package org.unicef.rapidreg.base.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.unicef.rapidreg.IntentStarter;
import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.cases.CasesSearchFragment;
import org.unicef.rapidreg.login.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_LOGOUT = 0;
    protected
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    IntentStarter intentStarter = new IntentStarter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        drawer.openDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_cases) {
            navigationView.getMenu().getItem(0).setChecked(true);
        } else if (id == R.id.nav_tracing) {
            navigationView.getMenu().getItem(1).setChecked(true);
        } else if (id == R.id.nav_sync) {
            navigationView.getMenu().getItem(2).setChecked(true);
        } else if (id == R.id.nav_logout) {
            navigationView.getMenu().getItem(3).setChecked(true);
            attemptlogout(this);
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //TODO: Put logout into basePresenter in future
    private void attemptlogout(BaseActivity currentActivity) {
        if (getSyncTask(currentActivity) != null) {
            createAlertDialog(currentActivity);
        } else {
            logOut(currentActivity);
        }
    }

    private void logOut(BaseActivity currentActivity) {
        PrimeroApplication context = (PrimeroApplication) currentActivity.getApplication();
        String message = context.getResources().getString(R.string.login_out_successful_text);
        context.setCurrentUser(null);
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        intentStarter.showLoginActivity(currentActivity);
    }

    private void createAlertDialog(BaseActivity currentActivity) {

    }

    // TODO: need to realise get in progress Sychronization tasks
    private Object getSyncTask(BaseActivity currentActivity) {
        return null;
    }
}
