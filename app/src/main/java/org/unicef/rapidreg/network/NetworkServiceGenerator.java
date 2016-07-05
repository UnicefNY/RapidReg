package org.unicef.rapidreg.network;

import android.content.Context;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import org.unicef.rapidreg.BuildConfig;
import org.unicef.rapidreg.R;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkServiceGenerator {
    //    public static final String API_BASE_URL = "http://10.29.3.184:3000";
    public static String apiBaseUrl = "https://10.29.3.184:8443";
    private static NetworkServiceGenerator self = null;

    private OkHttpClient.Builder httpClientBuilder;
    private Retrofit.Builder retrofitBuilder;

    public static NetworkServiceGenerator getInstance() {
        if (self == null) {
            self = new NetworkServiceGenerator();
        }
        return self;
    }


    public <S> S createService(Context context, Class<S> serviceClass) throws Exception {

        changeApiBaseUrl(apiBaseUrl);

        httpClientBuilder = new OkHttpClient.Builder();

        httpClientBuilder.sslSocketFactory(getSSLContext(context).getSocketFactory());
        httpClientBuilder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });

        if (BuildConfig.DEBUG) {
            httpClientBuilder.addNetworkInterceptor(new StethoInterceptor());
        }

        Retrofit retrofit = retrofitBuilder.client(httpClientBuilder.build()).build();

        return retrofit.create(serviceClass);
    }

    private SSLContext getSSLContext(Context context) throws Exception {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        InputStream cert = context.getResources().openRawResource(R.raw.primero);
        Certificate ca = cf.generateCertificate(cert);

        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);

        return sslContext;
    }

    public void changeApiBaseUrl(String newApiBaseUrl) {
        apiBaseUrl = newApiBaseUrl;

        retrofitBuilder = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(apiBaseUrl);
    }
}
