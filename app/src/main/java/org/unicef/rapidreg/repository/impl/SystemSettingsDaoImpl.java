package org.unicef.rapidreg.repository.impl;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.unicef.rapidreg.model.Case;
import org.unicef.rapidreg.model.Case_Table;
import org.unicef.rapidreg.model.SystemSettings;
import org.unicef.rapidreg.model.SystemSettings_Table;
import org.unicef.rapidreg.repository.SystemSettingsDao;

public class SystemSettingsDaoImpl implements SystemSettingsDao {
    @Override
    public SystemSettings getByServerUrl(String serverUrl) {
        return SQLite.select().from(SystemSettings.class).where(SystemSettings_Table.server_url.eq(serverUrl))
                .querySingle();
    }

    @Override
    public void save(SystemSettings systemSettings) {
        systemSettings.save();
    }

    @Override
    public void update(SystemSettings systemSettings) {
        systemSettings.update();
    }
}
