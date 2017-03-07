package org.unicef.rapidreg.repository;

import org.unicef.rapidreg.model.SystemSettings;

public interface SystemSettingsDao {
    SystemSettings getByServerUrl(String serverUrl);

    void save(SystemSettings systemSettings);

    void update(SystemSettings systemSettings);
}

