package org.unicef.rapidreg.childcase;

import org.unicef.rapidreg.base.RecordRegisterPresenter;
import org.unicef.rapidreg.forms.CaseFormRoot;
import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.CaseService;
import org.unicef.rapidreg.service.cache.ItemValues;

import java.io.IOException;
import java.util.ArrayList;

import javax.inject.Inject;

public class CaseRegisterPresenter extends RecordRegisterPresenter {

    private CaseService caseService;
    private CaseFormService caseFormService;

    @Inject
    public CaseRegisterPresenter(CaseService caseService, CaseFormService caseFormService) {
        this.caseService = caseService;
        this.caseFormService = caseFormService;
    }

    public Case saveCase(ItemValues itemValues, ArrayList<String> photoPaths) throws IOException {
        return caseService.saveOrUpdate(itemValues, photoPaths);
    }

    @Override
    public CaseFormRoot getCurrentForm() {
        return caseFormService.getCurrentForm();
    }
}
