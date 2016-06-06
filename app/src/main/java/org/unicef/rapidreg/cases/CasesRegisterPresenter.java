package org.unicef.rapidreg.cases;

import android.content.Context;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.model.form.childcase.CaseForm;
import org.unicef.rapidreg.service.CaseFormService;

public class CasesRegisterPresenter extends MvpBasePresenter<CasesRegisterView> {
    private CaseForm form;
    private CasesRegisterAdapter casesRegisterAdapter;

    private String value;

    public void initContext(Context context) {
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
        return CaseFormService.getInstance().getCurrentForm();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
