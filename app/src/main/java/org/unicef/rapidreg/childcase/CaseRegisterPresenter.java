package org.unicef.rapidreg.childcase;

import android.content.Context;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import org.unicef.rapidreg.forms.childcase.CaseField;
import org.unicef.rapidreg.forms.childcase.CaseFormRoot;
import org.unicef.rapidreg.service.CaseFormService;

import java.util.List;

public class CaseRegisterPresenter extends MvpBasePresenter<CaseRegisterView> {

    public void initContext(Context context, int position) {
        if (isViewAttached()) {
            CaseFormRoot form = CaseFormService.getInstance().getCurrentForm();
            if (form != null) {
                List<CaseField> fields = form.getSections().get(position).getFields();
                getView().initView(new CaseRegisterAdapter(context, fields, false));
            }
        }
    }
}
