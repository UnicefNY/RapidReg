package org.unicef.rapidreg.base;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.unicef.rapidreg.IntentSender;
import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.model.User;
import org.unicef.rapidreg.service.UserService;
import org.unicef.rapidreg.service.cache.PageModeCached;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int[][] COLOR_STATES = new int[][]{
            new int[]{android.R.attr.state_checked},
            new int[]{-android.R.attr.state_checked},};

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    @BindView(R.id.nav_view)
    protected NavigationView navigationView;
    @BindView(R.id.drawer_layout)
    protected DrawerLayout drawer;

    protected IntentSender intentSender = new IntentSender();

    protected ColorStateList caseColor;
    protected ColorStateList tracingColor;
    protected ColorStateList syncColor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        navigationView.setItemIconTintList(null);
        caseColor = generateColors(R.color.primero_green);
        tracingColor = generateColors(R.color.primero_red);
        syncColor = generateColors(R.color.primero_blue);
        navigationView.setItemTextColor(caseColor);

        View headerView = navigationView.getHeaderView(0);
        TextView textViewLoginUserLabel = (TextView) headerView.findViewById(R.id.login_user_label);
        TextView organizationView = (TextView) headerView.findViewById(R.id.organization);
        User currentUser = UserService.getInstance().getCurrentUser();
        if (currentUser != null) {
            String username = currentUser.getUsername();
            textViewLoginUserLabel.setText(username == null ? "" : username);

            String organisation = currentUser.getOrganisation();
            organizationView.setText(organisation == null ? "" : organisation);
        }
        TextView textViewLogoutLabel = (TextView) headerView.findViewById(R.id.logout_label);

        final BaseActivity baseActivity = this;
        textViewLogoutLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogout(baseActivity);
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        if (getIntent().getBooleanExtra(IntentSender.IS_OPEN_MENU, false)) {
            drawer.openDrawer(GravityCompat.START);
        } else {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            PageModeCached.setListMode();
        } else {
            processBackButton();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        drawer.closeDrawer(GravityCompat.START);
        switch (item.getItemId()) {
            case R.id.nav_cases:
                navCaseAction();
                navigationView.setItemTextColor(caseColor);
                break;
            case R.id.nav_tracing:
                navTracingAction();
                navigationView.setItemTextColor(tracingColor);
                break;
            case R.id.nav_sync:
                navigationView.setItemTextColor(syncColor);
                navSyncAction();
                break;
            default:
                break;
        }
        navigationView.getMenu().findItem(item.getItemId()).setChecked(true);
        return true;
    }


    public PrimeroApplication getContext() {
        return (PrimeroApplication) getApplication();
    }

    protected void logOut(BaseActivity currentActivity) {
        PrimeroApplication context = currentActivity.getContext();
        String message = context.getResources().getString(R.string.login_out_successful_text);
        UserService.getInstance().setCurrentUser(null);
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        intentSender.showLoginActivity(currentActivity);
    }

    //TODO: Put logout into basePresenter in future
    private void attemptLogout(BaseActivity currentActivity) {
        if (getContext().getSyncTask() != null) {
            createAlertDialog(currentActivity);
        } else {
            logOut(currentActivity);
        }
    }

    private void createAlertDialog(BaseActivity currentActivity) {
        //TODO: alert box
    }

    private ColorStateList generateColors(int resId) {
        int[] color = new int[]{
                ContextCompat.getColor(this, resId),
                ContextCompat.getColor(this, R.color.primero_font_medium),
        };

        return new ColorStateList(COLOR_STATES, color);
    }

    protected abstract void navSyncAction();

    protected abstract void navCaseAction();

    protected abstract void navTracingAction();

    protected abstract void processBackButton();

}
