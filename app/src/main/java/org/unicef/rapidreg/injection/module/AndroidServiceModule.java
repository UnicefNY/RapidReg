package org.unicef.rapidreg.injection.module;

import android.content.Context;
import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;

import org.unicef.rapidreg.injection.ApplicationContext;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AndroidServiceModule {
    @Provides
    @Singleton
    TelephonyManager provideTelephonyManager(@ApplicationContext Context context) {
        return (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    @Provides
    @Singleton
    ConnectivityManager provideConnectivityManager(@ApplicationContext Context context) {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }
}
