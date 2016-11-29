package org.unicef.rapidreg.base;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import org.unicef.rapidreg.model.CaseForm;
import org.unicef.rapidreg.model.TracingForm;
import org.unicef.rapidreg.model.User;
import org.unicef.rapidreg.service.CaseFormService;
import org.unicef.rapidreg.service.TracingFormService;
import org.unicef.rapidreg.service.UserService;

import javax.inject.Inject;

public class BasePresenter extends MvpBasePresenter<BaseView> {

    private UserService userService;
    private CaseFormService caseFormService;
    private TracingFormService tracingFormService;

    @Inject
    public BasePresenter(UserService userService, CaseFormService caseFormService, TracingFormService tracingFormService) {
        this.userService = userService;
        this.caseFormService = caseFormService;
        this.tracingFormService = tracingFormService;
    }

    public User getCurrentUser() {
        return userService.getCurrentUser();
    }

    public void setCurrentUser(User currentUser) {
        userService.setCurrentUser(currentUser);
    }

    public void saveCaseForm(CaseForm caseForm) {
        caseFormService.saveOrUpdateForm(caseForm);
    }

    public void saveTracingForm(TracingForm tracingForm) {
        tracingFormService.saveOrUpdateForm(tracingForm);
    }
}
