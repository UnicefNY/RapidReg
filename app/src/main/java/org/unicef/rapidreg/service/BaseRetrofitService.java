package org.unicef.rapidreg.service;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import org.unicef.rapidreg.BuildConfig;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class BaseRetrofitService {
    protected abstract String getBaseUrl();

    private Retrofit retrofit;

    private OkHttpClient getClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.readTimeout(90, TimeUnit.SECONDS);
        builder.writeTimeout(90, TimeUnit.SECONDS);
        builder.sslSocketFactory(getSSLContext().getSocketFactory());
        builder.hostnameVerifier((hostname, session) -> true);

        if (BuildConfig.DEBUG) {
            builder.addNetworkInterceptor(new StethoInterceptor());
        }
        return builder.build();
    }

    protected Retrofit createRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(getBaseUrl())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(getClient())
                .build();
        return retrofit;
    }

    private SSLContext getSSLContext() {
        try {
            KeyStore ks = KeyStore.getInstance("AndroidCAStore");
            if (ks != null) {
                ks.load(null, null);
            }

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(ks);

            // Create an SSLContext that uses our TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

            return sslContext;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}



