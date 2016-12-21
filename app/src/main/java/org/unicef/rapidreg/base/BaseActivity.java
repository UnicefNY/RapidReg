package org.unicef.rapidreg.base;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.MvpActivity;

import org.unicef.rapidreg.IntentSender;
import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.injection.component.ActivityComponent;
import org.unicef.rapidreg.injection.component.DaggerActivityComponent;
import org.unicef.rapidreg.injection.module.ActivityModule;
import org.unicef.rapidreg.model.User;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseActivity extends MvpActivity<BaseView, BasePresenter> implements NavigationView.OnNavigationItemSelectedListener {
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
    protected ColorStateList incidentColor;
    protected ColorStateList syncColor;

    @Inject
    BasePresenter basePresenter;

    @NonNull
    @Override
    public BasePresenter createPresenter() {
        return basePresenter;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getComponent().inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        navigationView.setItemIconTintList(null);
        caseColor = generateColors(R.color.primero_green);
        tracingColor = generateColors(R.color.primero_red);
        incidentColor = generateColors(R.color.black);
        syncColor = generateColors(R.color.primero_blue);
        navigationView.setItemTextColor(caseColor);

        View headerView = navigationView.getHeaderView(0);
        TextView textViewLoginUserLabel = (TextView) headerView.findViewById(R.id.login_user_label);
        TextView organizationView = (TextView) headerView.findViewById(R.id.organization);
        User currentUser = basePresenter.getCurrentUser();
        if (currentUser != null) {
            String username = currentUser.getUsername();
            textViewLoginUserLabel.setText(username);

            String organisation = currentUser.getOrganisation();
            organizationView.setText(organisation);
        }
        TextView textViewLogoutLabel = (TextView) headerView.findViewById(R.id.logout_label);
        textViewLogoutLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getContext().getSyncTask() == null) {
                    logOut();
                }
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

    public ActivityComponent getComponent() {
        return DaggerActivityComponent.builder()
                .activityModule(new ActivityModule(this))
                .applicationComponent(PrimeroApplication.get(this).getComponent())
                .build();
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
            case R.id.nav_incident:
                navIncidentAction();
                navigationView.setItemTextColor(incidentColor);
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

    protected void logOut() {
        basePresenter.logOut();
        String message = getContext().getResources().getString(R.string.login_out_successful_text);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        intentSender.showLoginActivity(this);
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

    protected abstract void navIncidentAction();

    protected abstract void processBackButton();

}
