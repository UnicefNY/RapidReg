package org.unicef.rapidreg.childcase;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import org.unicef.rapidreg.base.BaseActivity;
import org.unicef.rapidreg.childcase.config.PhotoConfig;
import org.unicef.rapidreg.event.NeedLoadFormsEvent;
import org.unicef.rapidreg.event.SaveCaseEvent;
import org.unicef.rapidreg.event.UpdateImageEvent;
import org.unicef.rapidreg.forms.CaseFormRoot;
import org.unicef.rapidreg.model.CaseForm;
import org.unicef.rapidreg.network.AuthService;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.CaseService;
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

public class CaseActivity extends BaseActivity {
    public static final String TAG = CaseActivity.class.getSimpleName();
    private DetailState textAreaState = DetailState.VISIBILITY;

    private MenuItem caseSaveMenu;
    private MenuItem caseSearchMenu;
    private MenuItem caseToggleMenu;

    private String imagePath;
    private CaseFeature currentFeature;

    private CompositeSubscription subscriptions;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subscriptions = new CompositeSubscription();
        initToolbar();
        turnToFeature(CaseFeature.LIST, null);

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (Activity.RESULT_OK != resultCode) {
            return;
        }

        if (PhotoUploadViewHolder.REQUEST_CODE_GALLERY == requestCode) {
            onSelectFromGalleryResult(data);

        } else if (PhotoUploadViewHolder.REQUEST_CODE_CAMERA == requestCode) {
            onCaptureImageResult();
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

    @Override
    protected void processBackButton() {
        if (currentFeature.isInListMode()) {
            logOut(this);
        } else if (currentFeature.isInEditMode()) {
            showQuitDialog(R.id.nav_cases);
        } else {
            CaseService.clearAudioFile();
            turnToFeature(CaseFeature.LIST, null);
        }
    }

    @Override
    protected void navCaseAction() {
        if (currentFeature.isInEditMode()) {
            showQuitDialog(R.id.nav_cases);
        } else {
            CaseService.clearAudioFile();
            turnToFeature(CaseFeature.LIST, null);
        }
    }

    @Override
    protected void navSyncAction() {
        if (currentFeature.isInEditMode()) {
            showQuitDialog(R.id.nav_sync);
        } else {
            CaseService.clearAudioFile();
            intentSender.showSyncActivity(this);
        }
    }

    public void turnToDetailOrEditPage(CaseFeature feature, long caseId) {
        try {

            Bundle args = new Bundle();
            args.putLong("case_id", caseId);

            currentFeature = feature;

            turnToFeature(feature, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void turnToFeature(CaseFeature feature, Bundle args) {
        currentFeature = feature;
        changeToolbarTitle(feature.getTitleId());
        changeToolbarIcon(feature);
        try {
            Fragment fragment = feature.getFragment();
            if (args != null) {
                fragment.setArguments(args);
            }
            navToFragment(fragment);
        } catch (Exception e) {
            throw new RuntimeException("Fragment navigation error", e);
        }
    }

    public CaseFeature getCurrentFeature() {
        return currentFeature;
    }

    private void showQuitDialog(final int clickedButton) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.quit)
                .setMessage(R.string.quit_without_saving)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        CaseService.clearAudioFile();
                        switch (clickedButton) {
                            case R.id.nav_cases:
                                turnToFeature(CaseFeature.LIST, null);
                                break;
                            case R.id.nav_sync:
                                intentSender.showSyncActivity(CaseActivity.this);
                                break;
                            default:
                                break;
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void initToolbar() {
        toolbar.inflateMenu(R.menu.toolbar_main);
        toolbar.setOnMenuItemClickListener(new CaseMenuItemListener());

        caseSaveMenu = toolbar.getMenu().findItem(R.id.save_case);
        caseSearchMenu = toolbar.getMenu().findItem(R.id.search);
        caseToggleMenu = toolbar.getMenu().findItem(R.id.toggle);
    }

    private String getOutputMediaFilePath() {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + File.separator + getApplicationContext().getPackageName());
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
        }
        return mediaStorageDir.getPath() + File.separator + System.currentTimeMillis() + ".jpg";
    }


    private class CaseMenuItemListener implements Toolbar.OnMenuItemClickListener {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.toggle:
                    showHideCaseDetail();
                    return true;
                case R.id.search:
                    turnToFeature(CaseFeature.SEARCH, null);
                    return true;
                case R.id.save_case:
                    return saveCase();
                default:
                    return false;
            }
        }
    }

    private void showHideCaseDetail() {
        textAreaState = textAreaState.getNextState();

        caseToggleMenu.setIcon(textAreaState.getResId());
        CaseListFragment caseListFragment = (CaseListFragment) getSupportFragmentManager()
                .findFragmentByTag(CaseListFragment.class.getSimpleName());
        caseListFragment.toggleMode(textAreaState.isDetailShow());
    }

    private void clearFocusToMakeLastFieldSaved() {
        CaseRegisterWrapperFragment fragment =
                (CaseRegisterWrapperFragment) getSupportFragmentManager()
                        .findFragmentByTag(CaseRegisterWrapperFragment.class.getSimpleName());

        if (fragment != null) {
            fragment.clearFocus();
        }
    }

    private boolean saveCase() {
        clearFocusToMakeLastFieldSaved();
        SaveCaseEvent event = new SaveCaseEvent();
        EventBus.getDefault().postSticky(event);
        turnToFeature(CaseFeature.LIST, null);
        return true;
    }


    private void hideAllToolbarIcons() {
        caseToggleMenu.setVisible(false);
        caseSearchMenu.setVisible(false);
        caseSaveMenu.setVisible(false);
    }

    private void changeToolbarIcon(CaseFeature feature) {
        hideAllToolbarIcons();

        switch (feature) {
            case LIST:
                caseToggleMenu.setVisible(true);
                caseSearchMenu.setVisible(true);
                break;
            case EDIT:
            case ADD:
                caseSaveMenu.setVisible(true);
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 1)
    public void onNeedLoadFormsEvent(final NeedLoadFormsEvent event) {

        EventBus.getDefault().removeStickyEvent(event);

        final Gson gson = new Gson();

        subscriptions.add(AuthService.getInstance().getFormRx(event.getCookie(),
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
                        CaseFormService.getInstance().saveOrUpdateCaseForm(caseForm);

                        Log.i(TAG, "load form successfully");

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.i(TAG, throwable.getMessage());
                    }
                }));

    }


    private void changeToolbarTitle(int resId) {
        toolbar.setTitle(resId);
    }

    private void navToFragment(Fragment target) {
        if (target != null) {
            String tag = target.getClass().getSimpleName();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_content, target, tag).commit();
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
