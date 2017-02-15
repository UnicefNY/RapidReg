package org.unicef.rapidreg.base;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.MvpActivity;

import org.greenrobot.eventbus.EventBus;
import org.unicef.rapidreg.IntentSender;
import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.childcase.CaseActivity;
import org.unicef.rapidreg.event.CreateIncidentThruGBVCaseEvent;
import org.unicef.rapidreg.injection.component.ActivityComponent;
import org.unicef.rapidreg.injection.component.DaggerActivityComponent;
import org.unicef.rapidreg.injection.module.ActivityModule;
import org.unicef.rapidreg.model.User;
import org.unicef.rapidreg.utils.Utils;

import javax.inject.Inject;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;
import static org.unicef.rapidreg.model.User.Role.GBV;

public abstract class BaseActivity extends MvpActivity<BaseView, BasePresenter> {

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

    @BindView(R.id.create_incident)
    protected ImageButton createIncidentBtn;

    @BindView(R.id.login_user_label)
    protected TextView textViewLoginUserLabel;

    @BindView(R.id.organization)
    protected TextView organizationView;

    @BindView(R.id.logout_label)
    protected ImageView textViewLogoutLabel;

    @BindView(R.id.nav_cases)
    protected TextView navCasesTV;

    @BindView(R.id.nav_tracing)
    protected TextView navTracingTV;

    @BindView(R.id.nav_incident)
    protected TextView navIncidentTV;

    @BindView(R.id.nav_sync)
    protected TextView navSyncTV;

    @BindColor(R.color.primero_green)
    protected ColorStateList caseColor;

    @BindColor(R.color.primero_red)
    protected ColorStateList tracingColor;

    @BindColor(R.color.primero_blue)
    protected ColorStateList incidentColor;

    @BindColor(R.color.black)
    protected ColorStateList syncColor;

    protected IntentSender intentSender = new IntentSender();

    protected DetailState detailState = DetailState.VISIBILITY;

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

        initToolbar();
        initNavigationHeader();
        initNavigationItemMenu();
        drawer.openDrawer(GravityCompat.START);
    }

    private void initNavigationHeader() {
        User currentUser = basePresenter.getCurrentUser();
        if (currentUser != null) {
            String username = currentUser.getUsername();
            textViewLoginUserLabel.setText(username);

            String organisation = currentUser.getOrganisation();
            organizationView.setText(organisation);
        }

        textViewLogoutLabel.setOnClickListener(view -> {
            if (getContext().getSyncTask() == null) {
                logOut();
            }
        });
    }

    protected void initNavigationItemMenu() {
        navCasesTV.setVisibility(GONE);
        navTracingTV.setVisibility(GONE);
        navIncidentTV.setVisibility(GONE);
        navSyncTV.setVisibility(GONE);

        User user = PrimeroAppConfiguration.getCurrentUser();
        if (user != null) {
            User.Role role = user.getRoleType();
            for (int resId : role.getResIds()) {
                findViewById(resId).setVisibility(View.VISIBLE);
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

    @OnClick(R.id.nav_cases)
    public void onNavCasesTVClick() {
        drawer.closeDrawer(GravityCompat.START);
        navCaseAction();
    }

    @OnClick(R.id.nav_tracing)
    public void onNavTracingButtonClick() {
        drawer.closeDrawer(GravityCompat.START);
        navTracingAction();
    }

    @OnClick(R.id.nav_incident)
    public void onNavIncidentButtonClick() {
        drawer.closeDrawer(GravityCompat.START);
        navIncidentAction();
    }

    @OnClick(R.id.nav_sync)
    public void onNavSyncButtonClick() {
        drawer.closeDrawer(GravityCompat.START);
        navSyncAction();
    }

    public PrimeroApplication getContext() {
        return (PrimeroApplication) getApplication();
    }

    protected void logOut() {
        basePresenter.logOut();
        Utils.showMessageByToast(this, R.string.login_out_successful_text, Toast.LENGTH_SHORT);
        intentSender.showLoginActivity(this);
    }

    private void initToolbar() {
        menu.setOnClickListener(view -> {
            if (getIntent().getBooleanExtra(IntentSender.IS_OPEN_MENU, false)) {
                drawer.openDrawer(GravityCompat.START);
            } else {
                drawer.closeDrawer(GravityCompat.START);
            }
        });
        showHideMenu.setOnClickListener(view -> showHideDetail());
        saveMenu.setOnClickListener(view -> save());
        searchMenu.setOnClickListener(view -> search());
    }

    @OnClick(R.id.create_incident)
    public void onCreateIncident() {
        CreateIncidentThruGBVCaseEvent event = new CreateIncidentThruGBVCaseEvent();
        EventBus.getDefault().postSticky(event);
    }

    protected void changeToolbarTitle(int resId) {
        toolbarTitle.setText(resId);
    }

    protected void changeToolbarIcon(Feature feature) {
        hideAllToolbarIcons();

        if (feature.isListMode()) {
            showHideMenu.setVisibility(GONE);
            searchMenu.setVisibility(View.VISIBLE);
            createIncidentBtn.setVisibility(GONE);
        } else if (feature.isEditMode()) {
            saveMenu.setVisibility(View.VISIBLE);
        } else if (feature.isDetailMode()) {
            enableCreateIncidentBtn();
        }
    }

    private void enableCreateIncidentBtn() {
        if (GBV == PrimeroAppConfiguration.getCurrentUser().getRoleType() &&
                this instanceof CaseActivity) {
            createIncidentBtn.setVisibility(View.VISIBLE);
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
        showHideMenu.setVisibility(GONE);
        searchMenu.setVisibility(GONE);
        saveMenu.setVisibility(GONE);
        createIncidentBtn.setVisibility(GONE);
    }

    protected void setNavSelectedMenu(int resId, ColorStateList color) {
        TextView view = (TextView) findViewById(resId);
        view.setTextColor(color);
        view.setBackgroundColor(getResources().getColor(R.color.lighter_gray));
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
