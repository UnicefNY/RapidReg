package org.unicef.rapidreg.cases;

import android.content.Context;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.model.forms.cases.CaseFormBean;

public class CasesRegisterPresenter extends MvpBasePresenter<CasesRegisterView> {

    private PrimeroApplication primeroApplication;
    private CaseFormBean form;
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

    private CaseFormBean loadCaseForms() {
        return primeroApplication.getCaseFormSections();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
