package org.unicef.rapidreg.login;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import org.unicef.rapidreg.tasks.LoginTask;

public class LoginPresenter extends MvpBasePresenter<LoginView> {

    private LoginTask loginTask;

    private void cancelLoginTaskIfRunning() {
        if (loginTask != null){
            loginTask.cancel(true);
        }
    }

    public void doLogin(){
        cancelLoginTaskIfRunning();

        loginTask = new LoginTask(new LoginTask.LoginTaskListener(){
            @Override
            public void onLoginSuccess(String messages) {
                if (isViewAttached()) {
                    getView().showLoginSuccessMessages(messages);
                }
            }

            @Override
            public void onLoginFailed(String messages) {
                if (isViewAttached()) {
                    getView().showLoginFailedMessages(messages);
                }
            }
        });
        loginTask.execute();
    }

    public void detachView(boolean retainPresenterInstance){
        super.detachView(retainPresenterInstance);
        if (!retainPresenterInstance){
            cancelLoginTaskIfRunning();
        }
    }
}
