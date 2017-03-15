package org.unicef.rapidreg;

import android.database.sqlite.SQLiteOpenHelper;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = PrimeroDatabaseConfiguration.NAME, version = PrimeroDatabaseConfiguration.VERSION)
public class PrimeroDatabaseConfiguration {
    public static final String NAME = "primero";
    public static final int VERSION = 1;
}
