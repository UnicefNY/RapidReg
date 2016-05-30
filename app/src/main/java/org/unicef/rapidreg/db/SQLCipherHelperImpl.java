package org.unicef.rapidreg.db;

import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.structure.database.DatabaseHelperListener;
import com.raizlabs.dbflow.android.sqlcipher.SQLCipherOpenHelper;

public class SQLCipherHelperImpl extends SQLCipherOpenHelper {

    public SQLCipherHelperImpl(DatabaseDefinition definition, DatabaseHelperListener listener) {
        super(definition, listener);
    }

    @Override
    protected String getCipherSecret() {
        return "primero-db-rules";
    }
}
