package org.unicef.rapidreg.event;

import org.unicef.rapidreg.model.User;

public class NeedCacheForOfflineEvent {
    private User user;

    public NeedCacheForOfflineEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
