package org.unicef.rapidreg.cases;

import android.content.Context;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import org.unicef.rapidreg.PrimeroApplication;
import org.unicef.rapidreg.model.forms.CaseFormRoot;

public class CasesRegisterPresenter extends MvpBasePresenter<CasesRegisterView> {

    private PrimeroApplication primeroApplication;
    private CaseFormRoot caseFormRoot;
    private CasesRegisterAdapter casesRegisterAdapter;


    public void initContext(Context context) {
        primeroApplication = (PrimeroApplication) context.getApplicationContext();
        if (isViewAttached()) {
            if ((caseFormRoot = loadCaseForms()) != null) {
                casesRegisterAdapter = new CasesRegisterAdapter(context, caseFormRoot.getCaseFormSections());
                getView().initView(casesRegisterAdapter);
                getView().expandAll(casesRegisterAdapter);
            }
        }
    }

    private CaseFormRoot loadCaseForms() {
        return primeroApplication.getCaseFormSections();
    }

}
