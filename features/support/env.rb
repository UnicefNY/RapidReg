require 'rubygems'
require 'appium_lib'
require_relative 'base_page'

APP_PATH = './app/build/outputs/apk/RapidReg-debug.apk'
desired_cps = {
    caps: {
        platformName: "android",
        deviceName: "Emulator",
        appPackage: "org.unicef.rapidreg.debug",
        appActivity: "org.unicef.rapidreg.login.LoginActivity",
        app: APP_PATH,
        noReset: true,
        fullReset: false
    },
    appium_lib: {
        sauce_username: nil,
        sauce_access_key: nil
    }
}

Appium::Driver.new(desired_cps)
Appium.promote_appium_methods RapidRegAppPage

Before { $driver.start_driver }
After { $driver.driver_quit }
