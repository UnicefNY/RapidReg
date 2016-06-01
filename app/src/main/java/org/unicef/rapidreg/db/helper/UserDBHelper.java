package org.unicef.rapidreg.db.helper;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.unicef.rapidreg.model.User;
import org.unicef.rapidreg.model.User_Table;

import java.util.List;

public class UserDBHelper {

    public static User getUserByName(String username) {
        return SQLite.select().from(User.class)
                .where(User_Table.user_name.eq(username)).querySingle();
    }

    public static List<User> getAllUsers() {
        return SQLite.select().from(User.class).queryList();
    }
}
