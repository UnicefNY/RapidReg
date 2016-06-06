package org.unicef.rapidreg.db;

import org.unicef.rapidreg.model.User;

import java.util.List;

public interface UserDao {
    User getUser(String username);

    long countUser();

    List<User> getAllUsers();

}

