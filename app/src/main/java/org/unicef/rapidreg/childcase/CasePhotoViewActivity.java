package org.unicef.rapidreg.childcase;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.childcase.CasePhotoViewAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CasePhotoViewActivity extends AppCompatActivity {
    @BindView(R.id.case_photo_view_slider)
    ViewPager viewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.case_photo_view_slider);
        ButterKnife.bind(this);

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        viewPager.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        viewPager.setAdapter(new CasePhotoViewAdapter(this,
                getIntent().getStringArrayListExtra("photos")));
        viewPager.setCurrentItem(getIntent().getIntExtra("position", 0));
    }
}
