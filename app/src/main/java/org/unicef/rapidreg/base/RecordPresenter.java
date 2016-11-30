package org.unicef.rapidreg.base;

import com.google.gson.Gson;

import org.unicef.rapidreg.forms.CaseFormRoot;
import org.unicef.rapidreg.forms.RecordForm;
import org.unicef.rapidreg.forms.TracingFormRoot;
import org.unicef.rapidreg.network.AuthService;
import org.unicef.rapidreg.service.UserService;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RecordPresenter extends BasePresenter {

    protected final Gson gson = new Gson();

    @Inject
    public RecordPresenter(UserService userService) {
        super(userService);
    }

    public void saveForm(RecordForm recordForm) {

    }
}
