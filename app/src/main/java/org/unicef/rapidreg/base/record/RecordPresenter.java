package org.unicef.rapidreg.base.record;

import com.google.gson.Gson;

import org.unicef.rapidreg.base.BasePresenter;
import org.unicef.rapidreg.forms.RecordForm;
import org.unicef.rapidreg.service.UserService;

import javax.inject.Inject;

public class RecordPresenter extends BasePresenter {
    protected final Gson gson = new Gson();

    @Inject
    public RecordPresenter() {}

    public void saveForm(RecordForm recordForm, String moduleId) {}
}
