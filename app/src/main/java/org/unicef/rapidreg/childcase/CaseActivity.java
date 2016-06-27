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
import android.widget.GridView;
import android.widget.Toast;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.view.BaseActivity;
import org.unicef.rapidreg.forms.childcase.CaseFormRoot;
import org.unicef.rapidreg.forms.childcase.CaseSection;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.service.cache.CaseFieldValueCache;
import org.unicef.rapidreg.service.cache.CasePhotoCache;
import org.unicef.rapidreg.utils.ImageCompressUtil;
import org.unicef.rapidreg.widgets.viewholder.PhotoUploadViewHolder;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CaseActivity extends BaseActivity {
    public final static String INTENT_KEY_CASE_MODE = "_case_mode";

    private String imagePath;

    private GridView photoGrid;
    private DetailState textAreaState = DetailState.VISIBILITY;

    public enum CaseMode {
        EDIT, ADD, LIST, DETAIL, SEARCH
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar.inflateMenu(R.menu.toolbar_main);
        toolbar.setOnMenuItemClickListener(new CaseMenuItemListener());
        toolbar.setTitle(R.string.cases);
        if (savedInstanceState == null) {
            redirectFragment(new CaseListFragment());
            setTopMenuItemsInCaseListPage();
            getIntent().removeExtra(INTENT_KEY_CASE_MODE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (TextUtils.isEmpty(imagePath)) {
            return;
        }
        photoGrid = (GridView) findViewById(R.id.photo_grid);

        List<Bitmap> previousPhotos = CasePhotoCache.getPhotosBits();
        Bitmap newPhoto = ImageCompressUtil.getThumbnail(
                ImageCompressUtil.compressBySize(imagePath, 512, 860), 80);
        previousPhotos.add(newPhoto);
        CasePhotoCache.addPhoto(newPhoto, imagePath);

        if (CasePhotoCache.isUnderLimit()) {
            previousPhotos.add(BitmapFactory.decodeResource(getResources(), R.drawable.photo_add));
        }
        photoGrid.setAdapter(new CasePhotoAdapter(this, previousPhotos));
        imagePath = null;
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
        }
    }

    private void onCaptureImageResult() {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(CasePhotoCache.MEDIA_PATH_FOR_CAMERA);
            imagePath = getOutputMediaFilePath();
            ImageCompressUtil.storeImage(bitmap, imagePath);
            bitmap.recycle();
        } catch (IOException e) {
            e.printStackTrace();
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

    @Override
    public void onBackPressed() {
        Serializable caseMode = getIntent().getSerializableExtra(INTENT_KEY_CASE_MODE);
        if (CaseActivity.CaseMode.LIST == caseMode) {
            moveTaskToBack(true);
        } else if (CaseMode.DETAIL == caseMode) {
            setTopMenuItemsInCaseListPage();
            super.onBackPressed();
        } else if (CaseMode.EDIT == caseMode) {
            new AlertDialog.Builder(this)
                    .setTitle("Quit")
                    .setMessage("Are you sure to quit without saving?")
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            redirectFragment(new CaseListFragment());
                            setTopMenuItemsInCaseListPage();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    }).show();
        } else {
            super.onBackPressed();
        }
    }

    private class CaseMenuItemListener implements Toolbar.OnMenuItemClickListener {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.toggle:
                    showHideCaseDetail();
                    return true;
                case R.id.search:
                    redirectFragment(new CaseSearchFragment());
                    setTopMenuItemsInCaseSearchPage();
                    return true;
                case R.id.save_case:
                    return saveCaseButtonAction();
                case R.id.edit_case:
                    return editCaseButtonAction();
                default:
                    return false;
            }
        }
    }

    private void showHideCaseDetail() {
        textAreaState = textAreaState.getNextState();

        MenuItem item = toolbar.getMenu().findItem(R.id.toggle);
        item.setIcon(textAreaState.getResId());
        CaseListFragment caseListFragment = (CaseListFragment) getSupportFragmentManager()
                .findFragmentByTag(CaseListFragment.class.getSimpleName());
        caseListFragment.toggleMode(textAreaState.isDetailShow());
    }

    private boolean saveCaseButtonAction() {
        if (validateRequiredField()) {
            Map<Bitmap, String> photoBitPaths = CasePhotoCache.getPhotoBitPaths();
            CaseService.getInstance().saveOrUpdateCase(CaseFieldValueCache.getValues(), photoBitPaths);
            redirectFragment(new CaseListFragment());
            setTopMenuItemsInCaseListPage();
        }
        return true;
    }

    private boolean editCaseButtonAction() {
        redirectFragment(new CaseRegisterWrapperFragment());
        getIntent().removeExtra(INTENT_KEY_CASE_MODE);
        setTopMenuItemsInCaseEditPage();
        return true;
    }

    private void redirectFragment(Fragment target) {
        String name = target.getClass().getSimpleName();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_content, target, name).commit();
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
                Toast.makeText(CaseActivity.this, "Some required field is not filled, please fill them", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
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
