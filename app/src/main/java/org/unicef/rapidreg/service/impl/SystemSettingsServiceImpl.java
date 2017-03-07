package org.unicef.rapidreg.service.impl;

import com.google.gson.JsonObject;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.model.SystemSettings;
import org.unicef.rapidreg.repository.SystemSettingsDao;
import org.unicef.rapidreg.repository.remote.SystemSettingRepository;
import org.unicef.rapidreg.service.BaseRetrofitService;
import org.unicef.rapidreg.service.SystemSettingsService;
import org.unicef.rapidreg.service.cache.GlobalLocationCache;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SystemSettingsServiceImpl extends BaseRetrofitService<SystemSettingRepository> implements
        SystemSettingsService {
    private SystemSettingsDao systemSettingsDao;

    public SystemSettingsServiceImpl(SystemSettingsDao systemSettingsDao) {
        this.systemSettingsDao = systemSettingsDao;
    }

    @Override
    public void initSystemSettings() {
        getRepository(SystemSettingRepository.class).getSystemSettings().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.isSuccessful()) {
                        JsonObject jsonObject = response.body().getAsJsonObject();
                        int districtLevel = jsonObject.getAsJsonObject("settings").getAsJsonObject
                                ("reporting_location_config")
                                .get("admin_level").getAsInt();
                        saveOrUpdateSystemSettings(districtLevel);
                        GlobalLocationCache.clearSimpleLocationCache();
                    }
                }, throwable -> {

                });
    }

    @Override
    public void setGlobalSystemSettings() {
        SystemSettings currentSystemSettings = systemSettingsDao.getByServerUrl(PrimeroAppConfiguration
                .getApiBaseUrl());
        PrimeroAppConfiguration.setCurrentSystemSettings(currentSystemSettings);
    }

    private void saveOrUpdateSystemSettings(int districtLevel) {
        SystemSettings currentSystemSettings = systemSettingsDao.getByServerUrl(PrimeroAppConfiguration
                .getApiBaseUrl());
        if (currentSystemSettings == null) {
            currentSystemSettings = new SystemSettings();
            currentSystemSettings.setServerUrl(PrimeroAppConfiguration.getApiBaseUrl());
            currentSystemSettings.setDistrictLevel(districtLevel);
            systemSettingsDao.save(currentSystemSettings);
        } else {
            currentSystemSettings.setDistrictLevel(districtLevel);
            systemSettingsDao.update(currentSystemSettings);
        }
        PrimeroAppConfiguration.setCurrentSystemSettings(currentSystemSettings);
    }

    @Override
    protected String getBaseUrl() {
        return PrimeroAppConfiguration.getApiBaseUrl();
    }
}
