package org.unicef.rapidreg.utils;

import android.util.Log;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.unicef.rapidreg.R;
import org.unicef.rapidreg.model.User;
import org.unicef.rapidreg.model.User_Table;

public class UserVerifier {
    public enum VerifiedCode {
        USER_DOES_NOT_EXIST(R.string.login_offline_no_user_text),
        PASSWORD_INCORRECT(R.string.login_failed_text),
        OK(R.string.login_offline_success_text);

        private int resId;

        VerifiedCode(int resId) {
            this.resId = resId;
        }

        public int getResId() {
            return resId;
        }
    }

    public static User getUser(String username) {
        return SQLite.select()
                .from(User.class)
                .where(User_Table.user_name.eq(username))
                .querySingle();
    }

    public static VerifiedCode verify(String username, String password) {
        User user = getUser(username);

        if (user == null) {
            return VerifiedCode.USER_DOES_NOT_EXIST;
        }

        if (EncryptHelper.isMatched(password, user.getPassword())) {
            return VerifiedCode.OK;
        } else {
            return VerifiedCode.PASSWORD_INCORRECT;
        }
    }
}
