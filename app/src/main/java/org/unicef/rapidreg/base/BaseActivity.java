package org.unicef.rapidreg.base;

import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.MvpActivity;

import org.unicef.rapidreg.IntentSender;
import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.record.recordlist.RecordListFragment;
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

    @BindView(R.id.nav_view)
    protected NavigationView navigationView;

    @BindView(R.id.drawer_layout)
    protected DrawerLayout drawer;

    @BindView(R.id.menu)
    protected ImageButton menu;

    @BindView(R.id.toolbar_title)
    protected TextView toolbarTitle;

    @BindView(R.id.toggle)
    protected ImageButton showHideMenu;

    @BindView(R.id.save)
    protected TextView saveMenu;

    @BindView(R.id.search)
    protected ImageButton searchMenu;

    protected IntentSender intentSender = new IntentSender();

    protected DetailState detailState = DetailState.VISIBILITY;

    protected ColorStateList caseColor;
    protected ColorStateList tracingColor;
    protected ColorStateList incidentColor;
    protected ColorStateList syncColor;

    protected boolean isMenuOpen;

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

        isMenuOpen = getIntent().getBooleanExtra(IntentSender.IS_OPEN_MENU, false);

        initToolbar();
        initNavigationItemMenu();

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
        ImageView textViewLogoutLabel = (ImageView) headerView.findViewById(R.id.logout_label);
        textViewLogoutLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getContext().getSyncTask() == null) {
                    logOut();
                }
            }
        });

        drawer.openDrawer(GravityCompat.START);
        navigationView.setNavigationItemSelectedListener(this);
    }

    protected void initNavigationItemMenu() {
        User user = PrimeroAppConfiguration.getCurrentUser();
        if (user != null) {
            User.Role role = user.getRoleType();
            for (int resId : role.getResIds()) {
                navigationView.inflateMenu(resId);
            }
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

    private void initToolbar() {
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getIntent().getBooleanExtra(IntentSender.IS_OPEN_MENU, false)) {
                    drawer.openDrawer(GravityCompat.START);
                } else {
                    drawer.closeDrawer(GravityCompat.START);
                }
            }
        });
        showHideMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHideDetail();
            }
        });
        saveMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
        searchMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });
    }

    protected void changeToolbarTitle(int resId) {
        toolbarTitle.setText(resId);
    }

    protected void changeToolbarIcon(Feature feature) {
        hideAllToolbarIcons();

        if (feature.isListMode()) {
            showHideMenu.setVisibility(View.GONE);
            searchMenu.setVisibility(View.VISIBLE);
        } else if (feature.isEditMode()) {
            saveMenu.setVisibility(View.VISIBLE);
        }
    }

    public void enableShowHideSwitcher() {
        showHideMenu.setVisibility(View.VISIBLE);
    }

    public void setShowHideSwitcherToShowState() {
        detailState = DetailState.VISIBILITY;
        showHideMenu.setBackgroundResource(detailState.getResId());
    }

    protected void hideAllToolbarIcons() {
        showHideMenu.setVisibility(View.GONE);
        searchMenu.setVisibility(View.GONE);
        saveMenu.setVisibility(View.GONE);
    }

    protected abstract void navSyncAction();

    protected abstract void navCaseAction();

    protected abstract void navTracingAction();

    protected abstract void navIncidentAction();

    protected abstract void processBackButton();

    protected abstract void search();

    protected abstract void save();

    protected abstract void showHideDetail();

    public enum DetailState {
        VISIBILITY(R.drawable.ic_visibility_white_24dp, true),
        INVISIBILITY(R.drawable.ic_visibility_off_white_24dp, false);

        private final int resId;
        private final boolean isDetailShow;

        DetailState(int resId, boolean isDetailShow) {
            this.resId = resId;
            this.isDetailShow = isDetailShow;
        }

        public DetailState getNextState() {
            return this == VISIBILITY ? INVISIBILITY : VISIBILITY;
        }

        public int getResId() {
            return resId;
        }

        public boolean isDetailShow() {
            return isDetailShow;
        }
    }

}
