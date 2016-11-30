package org.unicef.rapidreg.event;

import org.unicef.rapidreg.model.User;

public class CacheForOfflineEvent {
    private User user;

    public CacheForOfflineEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
