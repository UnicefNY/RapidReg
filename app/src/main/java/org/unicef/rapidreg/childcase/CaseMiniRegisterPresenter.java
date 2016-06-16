package org.unicef.rapidreg.childcase;

import android.content.Context;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import org.unicef.rapidreg.forms.childcase.CaseField;
import org.unicef.rapidreg.forms.childcase.CaseFormRoot;
import org.unicef.rapidreg.forms.childcase.CaseSection;
import org.unicef.rapidreg.service.CaseFormService;

import java.util.ArrayList;
import java.util.List;

public class CaseMiniRegisterPresenter extends MvpBasePresenter<CaseMiniRegisterView> {

    public void initContext(Context context) {
        if (isViewAttached()) {
            CaseFormRoot form = CaseFormService.getInstance().getCurrentForm();
            if (form != null) {
                List<CaseField> fields = new ArrayList<>();
                for (CaseSection section : form.getSections()) {
                    for (CaseField caseField : section.getFields()) {
                        if (caseField.isShowOnMiniForm()) {
                            fields.add(caseField);
                        }
                    }
                }
                getView().initView(new CaseMiniRegisterAdapter(context, -1, fields));
            }
        }
    }
}
