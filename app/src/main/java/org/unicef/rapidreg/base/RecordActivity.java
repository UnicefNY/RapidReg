package org.unicef.rapidreg.base;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.data.Blob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.event.NeedLoadFormsEvent;
import org.unicef.rapidreg.event.UpdateImageEvent;
import org.unicef.rapidreg.forms.CaseFormRoot;
import org.unicef.rapidreg.forms.TracingFormRoot;
import org.unicef.rapidreg.model.CaseForm;
import org.unicef.rapidreg.model.TracingForm;
import org.unicef.rapidreg.network.AuthService;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.RecordService;
import org.unicef.rapidreg.service.TracingFormService;
import org.unicef.rapidreg.utils.ImageCompressUtil;
import org.unicef.rapidreg.widgets.viewholder.PhotoUploadViewHolder;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public abstract class RecordActivity extends BaseActivity {
    public static final String TAG = RecordActivity.class.getSimpleName();

    protected DetailState textAreaState = DetailState.VISIBILITY;
    protected Feature currentFeature;

    protected MenuItem showHideMenu;
    protected MenuItem saveMenu;
    protected MenuItem searchMenu;

    private String imagePath;
    private CompositeSubscription subscriptions;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subscriptions = new CompositeSubscription();

        initToolbar();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
        subscriptions.clear();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (RESULT_OK != resultCode) {
            return;
        }

        if (PhotoUploadViewHolder.REQUEST_CODE_GALLERY == requestCode) {
            onSelectFromGalleryResult(data);

        } else if (PhotoUploadViewHolder.REQUEST_CODE_CAMERA == requestCode) {
            onCaptureImageResult();
        }
    }

    @Override
    protected void navSyncAction() {
        if (currentFeature.isEditMode()) {
            showQuitDialog(R.id.nav_sync);
        } else {
            RecordService.clearAudioFile();
            intentSender.showSyncActivity(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 1)
    public void onNeedLoadFormsEvent(final NeedLoadFormsEvent event) {
        EventBus.getDefault().removeStickyEvent(event);
        final Gson gson = new Gson();

        subscriptions.add(AuthService.getInstance().getCaseFormRx(event.getCookie(),
                Locale.getDefault().getLanguage(), true, "case")
                .flatMap(new Func1<CaseFormRoot, Observable<CaseFormRoot>>() {
                    @Override
                    public Observable<CaseFormRoot> call(CaseFormRoot caseFormRoot) {
                        if (caseFormRoot == null) {
                            return Observable.error(new Exception());
                        }
                        return Observable.just(caseFormRoot);
                    }
                })
                .retry(3)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<CaseFormRoot>() {
                    @Override
                    public void call(CaseFormRoot caseFormRoot) {

                        CaseFormRoot form = caseFormRoot;
                        CaseForm caseForm = new CaseForm(new Blob(gson.toJson(form).getBytes()));
                        CaseFormService.getInstance().saveOrUpdateForm(caseForm);

                        Log.i(TAG, "load form successfully");

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.i(TAG, throwable.getMessage());
                    }
                }));

        subscriptions.add(AuthService.getInstance().getTracingFormRx(event.getCookie(),
                Locale.getDefault().getLanguage(), true, "tracing_request")
                .flatMap(new Func1<TracingFormRoot, Observable<TracingFormRoot>>() {
                    @Override
                    public Observable<TracingFormRoot> call(TracingFormRoot tracingFormRoot) {
                        if (tracingFormRoot == null) {
                            return Observable.error(new Exception());
                        }
                        return Observable.just(tracingFormRoot);
                    }
                })
                .retry(3)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<TracingFormRoot>() {
                    @Override
                    public void call(TracingFormRoot tracingFormRoot) {
                        TracingFormRoot form = tracingFormRoot;

                        TracingForm tracingForm = new TracingForm(new Blob(gson.toJson(form).getBytes()));
                        TracingFormService.getInstance().saveOrUpdateForm(tracingForm);

                        Log.i(TAG, "load form successfully");

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.i(TAG, throwable.getMessage());
                    }
                }));
    }

    public Feature getCurrentFeature() {
        return currentFeature;
    }

    public void turnToFeature(Feature feature, Bundle args, int[] animIds) {
        currentFeature = feature;
        changeToolbarTitle(feature.getTitleId());
        changeToolbarIcon(feature);
        try {
            Fragment fragment = feature.getFragment();
            if (args != null) {
                fragment.setArguments(args);
            }
            navToFragment(fragment, animIds);
        } catch (Exception e) {
            throw new RuntimeException("Fragment navigation error", e);
        }
    }

    protected void changeToolbarTitle(int resId) {
        toolbar.setTitle(resId);
    }

    protected void changeToolbarIcon(Feature feature) {
        hideAllToolbarIcons();

        if (feature.isListMode()) {
            showHideMenu.setVisible(true);
            searchMenu.setVisible(true);
        } else if (feature.isEditMode()) {
            saveMenu.setVisible(true);
        }
    }

    private void onSelectFromGalleryResult(Intent data) {
        Uri uri = data.getData();
        if (!TextUtils.isEmpty(uri.getAuthority())) {
            Cursor cursor = getContentResolver().query(uri,
                    new String[]{MediaStore.Images.Media.DATA}, null, null, null);
            cursor.moveToFirst();
            imagePath = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Images.Media.DATA));
            cursor.close();
            postSelectedImagePath();
        }
    }

    private String getOutputMediaFilePath() {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + File.separator + getApplicationContext().getPackageName());
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
        }
        return mediaStorageDir.getPath() + File.separator + System.currentTimeMillis() + ".jpg";
    }

    private void onCaptureImageResult() {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(PhotoConfig.MEDIA_PATH_FOR_CAMERA);
            imagePath = getOutputMediaFilePath();
            ImageCompressUtil.storeImage(bitmap, imagePath);
            bitmap.recycle();
            postSelectedImagePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void postSelectedImagePath() {
        UpdateImageEvent event = new UpdateImageEvent();
        event.setImagePath(imagePath);
        EventBus.getDefault().postSticky(event);
    }

    private void initToolbar() {
        toolbar.inflateMenu(R.menu.toolbar_main);
        toolbar.setOnMenuItemClickListener(new MenuItemListener());

        saveMenu = toolbar.getMenu().findItem(R.id.save);
        searchMenu = toolbar.getMenu().findItem(R.id.search);
        showHideMenu = toolbar.getMenu().findItem(R.id.toggle);
    }

    private void hideAllToolbarIcons() {
        showHideMenu.setVisible(false);
        searchMenu.setVisible(false);
        saveMenu.setVisible(false);
    }

    private void navToFragment(Fragment target, int[] animIds) {
        if (target != null) {
            String tag = target.getClass().getSimpleName();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (animIds != null) {
                transaction.setCustomAnimations(animIds[0], animIds[1]);
            }
            transaction.replace(R.id.fragment_content, target, tag).commit();
        }
    }

    protected abstract void showHideDetail();

    protected abstract void search();

    protected abstract void save();

    protected abstract void showQuitDialog(int resId);

    private class MenuItemListener implements Toolbar.OnMenuItemClickListener {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.toggle:
                    showHideDetail();
                    return true;
                case R.id.search:
                    search();
                    return true;
                case R.id.save:
                    save();
                    return true;
                default:
                    return false;
            }
        }
    }

    public enum DetailState {
        VISIBILITY(R.drawable.visible, true),
        INVISIBILITY(R.drawable.invisible, false);

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
