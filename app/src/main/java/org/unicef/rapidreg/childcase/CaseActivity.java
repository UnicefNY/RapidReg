package org.unicef.rapidreg.childcase;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Toast;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.view.BaseActivity;
import org.unicef.rapidreg.forms.childcase.CaseFormRoot;
import org.unicef.rapidreg.forms.childcase.CaseSection;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.CaseService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.unicef.rapidreg.service.CaseService.CaseValues;

public class CaseActivity extends BaseActivity {
    public final static String INTENT_KEY_CASE_MODE = "_case_mode";

    public enum CaseMode {
        EDIT, ADD, LIST, DETAIL
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
    public void onBackPressed() {
        Serializable caseMode = getIntent().getSerializableExtra(INTENT_KEY_CASE_MODE);
        if (CaseActivity.CaseMode.LIST == caseMode) {
            moveTaskToBack(true);
        } else if (CaseMode.DETAIL == caseMode) {
            setTopMenuItemsInCaseListPage();
            super.onBackPressed();
        } else if (CaseMode.ADD == caseMode || CaseMode.EDIT == caseMode) {
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

    private boolean saveCaseButtonAction() {
        if (validateRequiredField()) {
            CaseService.getInstance().saveOrUpdateCase(CaseValues.getValues());
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
        transaction.replace(R.id.fragment_content, target, name)
                .commit();
        resetBarButtonsIfNeeded();
    }

    private void resetBarButtonsIfNeeded() {
        if (isListFragmentVisible()) {
            setTopMenuItemsInCaseAdditionPage();
        }
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
            if (TextUtils.isEmpty(CaseValues.getValues().get(field))) {
                Toast.makeText(CaseActivity.this, "Some required field is not filled, please fill them", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }
}
