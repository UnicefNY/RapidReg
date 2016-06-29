package org.unicef.rapidreg.base.view;

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
import org.unicef.rapidreg.service.UserService;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int[][] COLOR_STATES = new int[][]{
            new int[]{android.R.attr.state_checked},
            new int[]{-android.R.attr.state_checked},};

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    @BindView(R.id.nav_view)
    protected NavigationView navigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    IntentSender intentSender = new IntentSender();

    private ColorStateList caseColor;
    private ColorStateList trColor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        navigationView.setItemIconTintList(null);
        caseColor = generateColors(R.color.ftn_green);
        trColor = generateColors(R.color.ftn_red);
        navigationView.setItemTextColor(caseColor);

        View headerView = navigationView.getHeaderView(0);
        TextView textViewLoginUserLabel = (TextView) headerView.findViewById(R.id.login_user_label);
        textViewLoginUserLabel.setText(getIntent().getStringExtra(IntentSender.KEY_LOGIN_USER));
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
        drawer.openDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            processBackButton();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        drawer.closeDrawer(GravityCompat.START);
        if (id == R.id.nav_cases) {
            navCaseAction();
            navigationView.setItemTextColor(caseColor);
        } else if (id == R.id.nav_tracing) {
            navigationView.setItemTextColor(trColor);
        }
        navigationView.getMenu().findItem(id).setChecked(true);
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
                ContextCompat.getColor(this, R.color.font_medium),
        };

        return new ColorStateList(COLOR_STATES, color);
    }

    protected abstract void navCaseAction();

    protected abstract void processBackButton();
}
