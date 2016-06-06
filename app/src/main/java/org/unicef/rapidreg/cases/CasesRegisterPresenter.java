package org.unicef.rapidreg.cases;

import android.content.Context;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.model.forms.cases.CaseForm;

public class CasesRegisterPresenter extends MvpBasePresenter<CasesRegisterView> {

    private PrimeroApplication primeroApplication;
    private CaseForm form;
    private CasesRegisterAdapter casesRegisterAdapter;

    private String value;

    public void initContext(Context context) {
        primeroApplication = (PrimeroApplication) context.getApplicationContext();
        if (isViewAttached()) {
            if ((form = loadCaseForms()) != null) {
                casesRegisterAdapter = new CasesRegisterAdapter(context,
                        form.getSections());
                getView().initView(casesRegisterAdapter, form);
                getView().expandAll(casesRegisterAdapter);
            }
        }
    }

    private CaseForm loadCaseForms() {
        return primeroApplication.getCaseFormSections();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
