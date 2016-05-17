package org.unicef.rapidreg.event;

import android.content.Context;

public class NeedDoLoginOffLineEvent {
    public Context context;
    public String username;
    public String password;

    public NeedDoLoginOffLineEvent(Context context, String username, String password) {
        this.context = context;
        this.username = username;
        this.password = password;
    }
}
