package org.unicef.rapidreg.childcase;

import android.content.Context;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import org.unicef.rapidreg.forms.childcase.CaseFormRoot;
import org.unicef.rapidreg.service.CaseFormService;

public class CasesRegisterPresenter extends MvpBasePresenter<CasesRegisterView> {
    private CaseFormRoot form;
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

    private CaseFormRoot loadCaseForms() {
        return CaseFormService.getInstance().getCurrentForm();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
