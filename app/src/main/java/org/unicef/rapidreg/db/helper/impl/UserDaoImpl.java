package org.unicef.rapidreg.db.helper.impl;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.unicef.rapidreg.db.helper.UserDao;
import org.unicef.rapidreg.model.User;
import org.unicef.rapidreg.model.User_Table;

import java.util.List;

public class UserDaoImpl implements UserDao {

    @Override
    public User getUser(String username) {
        return SQLite.select().from(User.class).where(User_Table.user_name.eq(username))
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
}
