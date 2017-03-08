package org.unicef.rapidreg.service.impl;

import android.util.Log;

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
    private static final String TAG = SystemSettingsService.class.getSimpleName();

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
                    Log.e(TAG, "Init system settings error->" + throwable.getMessage());
                });
    }

    @Override
    public void setGlobalSystemSettings() {
        SystemSettings currentSystemSettings = systemSettingsDao.getByServerUrl(PrimeroAppConfiguration
                .getApiBaseUrl());
        if (currentSystemSettings == null) {
            currentSystemSettings = new SystemSettings();
            currentSystemSettings.setDistrictLevel(PrimeroAppConfiguration.DEFAULT_DISTRICT_LEVEL);
            currentSystemSettings.setServerUrl(PrimeroAppConfiguration.getApiBaseUrl());
            systemSettingsDao.save(currentSystemSettings);
        }
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
    }

    @Override
    protected String getBaseUrl() {
        return PrimeroAppConfiguration.getApiBaseUrl();
    }
}
