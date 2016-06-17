package org.unicef.rapidreg.childcase;

import android.content.Context;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.base.view.BaseActivity;
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
                if (fields.size() != 0) {
                    getView().initView(new CaseMiniRegisterAdapter(context, -1, fields));
                } else {
                    ((BaseActivity) context).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_content, new CaseRegisterWrapperFragment())
                            .addToBackStack(null)
                            .commit();
                    Toast.makeText(context, "There is no mini form!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
