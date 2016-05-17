package org.unicef.rapidreg.event;

import org.unicef.rapidreg.model.User;

public class NeedCacheForOfflineEvent {
    public User user;

    public NeedCacheForOfflineEvent(User user) {
        this.user = user;
    }
}
