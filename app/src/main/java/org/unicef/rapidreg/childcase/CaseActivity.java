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
import org.unicef.rapidreg.utils.ImageCompressUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CaseActivity extends BaseActivity {
    public final static String INTENT_KEY_CASE_MODE = "_case_mode";

    private final int IMAGE_OPEN = 1;
    private String imagePath;

    private GridView photoGrid;
    private DetailState textAreaState = DetailState.VISIBILITY;

    public enum CaseMode {
        EDIT, LIST, DETAIL
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar.inflateMenu(R.menu.toolbar_main);
        toolbar.setOnMenuItemClickListener(new CaseMenuItemListener());
        toolbar.setTitle("Cases");
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

        List<Bitmap> previousPhotos = CaseService.CaseValues.getPhotosBits();

        Bitmap newPhoto = ImageCompressUtil.getThumbnail(getContentResolver(), imagePath);
        previousPhotos.add(newPhoto);

        Bitmap addPhotoIcon = BitmapFactory.decodeResource(getResources(), R.drawable.photo_add);
        previousPhotos.add(addPhotoIcon);

        CaseService.CaseValues.addPhoto(newPhoto, imagePath);

        photoGrid.setAdapter(new CasePhotoAdapter(this, previousPhotos));
        imagePath = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode && IMAGE_OPEN == requestCode) {
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
        super.onActivityResult(requestCode, resultCode, data);
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
                    showOrHideCaseDetail();
                    return true;
                case R.id.search:
                    redirectFragment(new CaseSearchFragment());
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

    private void showOrHideCaseDetail() {
        textAreaState = textAreaState.getNextState();

        MenuItem item = toolbar.getMenu().findItem(R.id.toggle);
        item.setIcon(textAreaState.getResId());

        CaseListFragment caseListFragment = (CaseListFragment) getSupportFragmentManager()
                .findFragmentByTag(CaseListFragment.class.getSimpleName());
        caseListFragment.toggleMode(textAreaState.isDetailShow());
    }

    private boolean saveCaseButtonAction() {
        if (validateRequiredField()) {
            CaseService.getInstance().saveOrUpdateCase(CaseService.CaseValues.getValues());
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
        resetBarButtonsIfNeeded();
    }

    private void resetBarButtonsIfNeeded() {
    }

    private boolean isListFragmentVisible() {
        Fragment listFragment = getSupportFragmentManager()
                .findFragmentByTag(CaseListFragment.class.getSimpleName());

        return listFragment != null && listFragment.isVisible();
    }

    private boolean validateRequiredField() {
        CaseFormRoot caseForm = CaseFormService.getInstance().getCurrentForm();
        List<String> requiredFieldNames = new ArrayList<>();

        for (CaseSection section : caseForm.getSections()) {
            Collections.addAll(requiredFieldNames, CaseService.getInstance()
                    .fetchRequiredFiledNames(section.getFields()).toArray(new String[0]));
        }

        for (String field : requiredFieldNames) {
            if (TextUtils.isEmpty(CaseService.CaseValues.getValues().get(field))) {
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
