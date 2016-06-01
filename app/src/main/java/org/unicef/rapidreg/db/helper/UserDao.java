package org.unicef.rapidreg.db.helper;


import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.unicef.rapidreg.model.User;

import java.util.List;

public interface UserDao {
    User getUser(String username);

    long countUser();

    List<User> getAllUsers();
}
