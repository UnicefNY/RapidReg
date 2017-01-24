package org.unicef.rapidreg.repository.impl;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.unicef.rapidreg.model.User;
import org.unicef.rapidreg.model.User_Table;
import org.unicef.rapidreg.repository.UserDao;

import java.util.List;

import javax.inject.Inject;

public class UserDaoImpl implements UserDao {
    @Inject
    public UserDaoImpl() {
    }

    @Override
    public User getUser(String username, String url) {
        return SQLite.select().from(User.class)
                .where(User_Table.user_name.eq(username))
                .and(User_Table.server_url.eq(url))
                .querySingle();
    }

    @Override
    public long countUser() {
        return SQLite.select().from(User.class).count();
    }

    @Override
    public List<User> getAllUsers() {
        return SQLite.select().from(User.class).queryList();
    }

    @Override
    public void saveOrUpdateUser(User user) {
        User existingUser = getUser(user.getUsername(), user.getServerUrl());
        if (existingUser == null) {
            user.save();
        } else {
            existingUser.updateFields(user);
            existingUser.update();
        }
    }
}
