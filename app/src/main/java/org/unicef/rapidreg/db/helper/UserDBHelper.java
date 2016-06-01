package org.unicef.rapidreg.db.helper;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.unicef.rapidreg.model.User;
import org.unicef.rapidreg.model.User_Table;

public class UserDBHelper {
    public static User getUser(String username) {
        return SQLite.select()
                .from(User.class)
                .where(User_Table.user_name.eq(username))
                .querySingle();
    }
}
