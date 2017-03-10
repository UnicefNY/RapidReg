package org.unicef.rapidreg.service;

import org.unicef.rapidreg.model.SystemSettings;

import rx.Observable;

public interface SystemSettingsService {
    Observable<SystemSettings> getSystemSettings();

    void setGlobalSystemSettings();

    void saveOrUpdateSystemSettings(SystemSettings systemSettings);
}


