package org.unicef.rapidreg.repository;

import org.unicef.rapidreg.model.User;

import java.util.List;

public interface UserDao {
    User getUser(String username, String url);

    long countUser();

    List<User> getAllUsers();

    void saveOrUpdateUser(User user);
}

