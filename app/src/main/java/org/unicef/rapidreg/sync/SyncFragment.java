package org.unicef.rapidreg.sync;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hannesdorfmann.mosby.mvp.MvpFragment;

import org.unicef.rapidreg.R;

public class SyncFragment extends MvpFragment<SyncView, SyncPresenter> implements SyncView{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sync, container, false);
    }

    @Override
    public SyncPresenter createPresenter() {
        return new SyncPresenter();
    }
}
