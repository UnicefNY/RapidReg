package org.unicef.rapidreg.base.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import org.unicef.rapidreg.R;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }
}
