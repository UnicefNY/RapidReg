package org.unicef.rapidreg.service.impl;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.repository.remote.SystemSettingRepository;
import org.unicef.rapidreg.service.BaseRetrofitService;
import org.unicef.rapidreg.service.SystemSettingsService;

public class SystemSettingsServiceImpl extends BaseRetrofitService<SystemSettingRepository> implements
        SystemSettingsService {
    public SystemSettingsServiceImpl() {
    }

    @Override
    public void initSystemSettings() {
        getRepository(SystemSettingRepository.class).getSystemSettings();
    }

    @Override
    protected String getBaseUrl() {
        return PrimeroAppConfiguration.getApiBaseUrl();
    }
}
