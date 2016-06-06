package org.unicef.rapidreg.event;

import android.content.Context;

public class NeedDoLoginOffLineEvent {
    private Context context;
    private String username;
    private String password;

    public NeedDoLoginOffLineEvent(Context context, String username, String password) {
        this.context = context;
        this.username = username;
        this.password = password;
    }

    public Context getContext() {
        return context;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
