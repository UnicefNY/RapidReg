package org.unicef.rapidreg.tasks;

import android.os.AsyncTask;

import org.unicef.rapidreg.model.Root;
import org.unicef.rapidreg.network.NetworkService;

import java.io.IOException;

public class LoginTask extends AsyncTask<Void, Void, String> {

    public interface LoginTaskListener {
        public void onLoginSuccess(String messages);
        public void onLoginFailed(String messages);
    }

    public static final String LOGIN_SUCCESS_MESSAGE = "Login success!";
    public static final String LOGIN_FAILED_MESSAGE = "Login failed!";

    private LoginTaskListener listener;

    public LoginTask(LoginTaskListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            Root root = NetworkService.doLogin().execute().body();
        } catch (IOException e) {
            listener.onLoginFailed(LOGIN_FAILED_MESSAGE);
        }
        return LOGIN_SUCCESS_MESSAGE;
    }

    @Override
    protected void onPostExecute(String message) {
        listener.onLoginSuccess(message);
    }

}
