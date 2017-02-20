package org.unicef.rapidreg.tracing.tracingphoto;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.unicef.rapidreg.IntentSender;
import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.injection.component.ActivityComponent;
import org.unicef.rapidreg.injection.component.DaggerActivityComponent;
import org.unicef.rapidreg.injection.module.ActivityModule;
import org.unicef.rapidreg.login.AccountManager;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TracingPhotoViewActivity extends AppCompatActivity {
    @BindView(R.id.record_photo_view_slider)
    ViewPager viewPager;

    @Inject
    TracingPhotoViewAdapter tracingPhotoViewAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doCloseIfNotLogin();

        setContentView(R.layout.record_photo_view_slider);

        getComponent().inject(this);
        ButterKnife.bind(this);

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        viewPager.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        tracingPhotoViewAdapter.setPaths(getIntent().getStringArrayListExtra("photos"));
        viewPager.setAdapter(tracingPhotoViewAdapter);
        viewPager.setCurrentItem(getIntent().getIntExtra("position", 0));
    }

    @Override
    protected void onStart() {
        super.onStart();
        doCloseIfNotLogin();
    }

    @Override
    protected void onResume() {
        super.onResume();
        doCloseIfNotLogin();
    }

    private void doCloseIfNotLogin() {
        if (!AccountManager.isSignIn()) {
            AccountManager.doSignOut();
            new IntentSender().showLoginActivity(this);
            finish();
            return;
        }
    }

    public ActivityComponent getComponent() {
        return DaggerActivityComponent.builder()
                .applicationComponent(PrimeroApplication.get(getApplicationContext()).getComponent())
                .activityModule(new ActivityModule(this))
                .build();
    }
}
