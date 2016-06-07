package org.unicef.rapidreg.childcase;

import android.content.Context;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import org.unicef.rapidreg.forms.childcase.CaseField;
import org.unicef.rapidreg.forms.childcase.CaseFormRoot;
import org.unicef.rapidreg.service.CaseFormService;

import java.util.List;

public class CasesRegisterPresenter extends MvpBasePresenter<CasesRegisterView> {
    private CaseFormRoot form;
    private CasesRegisterAdapter casesRegisterAdapter;

    private String value;

    public void initContext(Context context, int position) {
        if (isViewAttached()) {
            if ((form = loadCaseForms()) != null) {
                List<CaseField> fields = form.getSections().get(position).getFields();
                casesRegisterAdapter = new CasesRegisterAdapter(context, -1, fields);
                getView().initView(casesRegisterAdapter);
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
