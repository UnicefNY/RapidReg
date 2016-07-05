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
import android.view.MenuItem;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.view.BaseActivity;
import org.unicef.rapidreg.event.SaveCaseEvent;
import org.unicef.rapidreg.event.UpdateImageEvent;
import org.unicef.rapidreg.forms.childcase.CaseFormRoot;
import org.unicef.rapidreg.forms.childcase.CaseSection;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.service.cache.CaseFieldValueCache;
import org.unicef.rapidreg.service.cache.CasePhotoCache;
import org.unicef.rapidreg.service.cache.SubformCache;
import org.unicef.rapidreg.utils.ImageCompressUtil;
import org.unicef.rapidreg.widgets.viewholder.PhotoUploadViewHolder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CaseActivity extends BaseActivity {
    private DetailState textAreaState = DetailState.VISIBILITY;

    private MenuItem caseSaveMenu;
    private MenuItem caseSearchMenu;
    private MenuItem caseToggleMenu;

    private String imagePath;
    private CaseFeature currentFeature;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initToolbar();
        turnToFeature(CaseFeature.LIST);
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
            Bitmap bitmap = BitmapFactory.decodeFile(CasePhotoCache.MEDIA_PATH_FOR_CAMERA);
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
            CaseFieldValueCache.clearAudioFile();
            turnToFeature(CaseFeature.LIST);
        }
    }

    @Override
    protected void navCaseAction() {
        if (currentFeature.isInEditMode()) {
            showQuitDialog(R.id.nav_cases);
        } else {
            CaseFieldValueCache.clearAudioFile();
            turnToFeature(CaseFeature.LIST);
        }
    }


    @Override
    protected void navSyncAction() {
        if (currentFeature.isInEditMode()) {
            showQuitDialog(R.id.nav_sync);
        } else {
            CaseFieldValueCache.clearAudioFile();
            intentSender.showSyncActivity(this);
        }
    }

    public void turnToFeature(CaseFeature feature) {
        currentFeature = feature;
        changeToolbarTitle(feature.getTitleId());
        changeToolbarIcon(feature);
        try {
            navToFragment(feature.getFragment());
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
                        CaseFieldValueCache.clearAudioFile();
                        switch (clickedButton) {
                            case R.id.nav_cases:
                                turnToFeature(CaseFeature.LIST);
                                break;
                            case R.id.nav_sync :
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
                    turnToFeature(CaseFeature.SEARCH);
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

        if (validateRequiredField()) {
            SaveCaseEvent event = new SaveCaseEvent();
            EventBus.getDefault().postSticky(event);
            turnToFeature(CaseFeature.LIST);
        }
        return true;
    }

    private boolean validateRequiredField() {
        CaseFormRoot caseForm = CaseFormService.getInstance().getCurrentForm();
        List<String> requiredFieldNames = new ArrayList<>();

        for (CaseSection section : caseForm.getSections()) {
            Collections.addAll(requiredFieldNames, CaseService.getInstance()
                    .fetchRequiredFiledNames(section.getFields()).toArray(new String[0]));
        }

        for (String field : requiredFieldNames) {
            if (TextUtils.isEmpty(CaseFieldValueCache.getValues().get(field))) {
                Toast.makeText(CaseActivity.this, R.string.required_field_is_not_filled,
                        Toast.LENGTH_LONG).show();
                return false;
            }
        }
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
