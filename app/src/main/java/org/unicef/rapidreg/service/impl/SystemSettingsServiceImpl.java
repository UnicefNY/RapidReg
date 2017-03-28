package org.unicef.rapidreg.service.impl;

import com.google.gson.JsonObject;

import org.unicef.rapidreg.PrimeroAppConfiguration;
import org.unicef.rapidreg.model.SystemSettings;
import org.unicef.rapidreg.repository.SystemSettingsDao;
import org.unicef.rapidreg.repository.remote.SystemSettingRepository;
import org.unicef.rapidreg.service.BaseRetrofitService;
import org.unicef.rapidreg.service.SystemSettingsService;

import java.util.concurrent.TimeUnit;

import rx.Observable;
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
    public Observable<SystemSettings> getSystemSettings() {
        return getRepository(SystemSettingRepository.class).getSystemSettings(PrimeroAppConfiguration.getCookie())
                .map(response -> {
                    SystemSettings currentSystemSettings = new SystemSettings();
                    currentSystemSettings.setServerUrl(PrimeroAppConfiguration.getApiBaseUrl());
                    if (response.isSuccessful()) {
                        JsonObject jsonObject = response.body().getAsJsonObject();
                        int districtLevel = jsonObject.getAsJsonObject("settings").getAsJsonObject
                                ("reporting_location_config")
                                .get("admin_level").getAsInt();
                        currentSystemSettings.setDistrictLevel(districtLevel);
                    } else {
                        currentSystemSettings.setDistrictLevel(PrimeroAppConfiguration.DEFAULT_DISTRICT_LEVEL);
                    }
                    return currentSystemSettings;
                })
                .retry(3)
                .timeout(60, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
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

    @Override
    public void saveOrUpdateSystemSettings(SystemSettings systemSettings) {
        SystemSettings currentSystemSettings = systemSettingsDao.getByServerUrl(systemSettings.getServerUrl());
        if (currentSystemSettings == null) {
            systemSettingsDao.save(systemSettings);
        } else {
            currentSystemSettings.setDistrictLevel(systemSettings.getDistrictLevel());
            systemSettingsDao.update(currentSystemSettings);
        }
    }

    @Override
    protected String getBaseUrl() {
        return PrimeroAppConfiguration.getApiBaseUrl();
    }
}
