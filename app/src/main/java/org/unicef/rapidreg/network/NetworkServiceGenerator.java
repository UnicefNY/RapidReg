package org.unicef.rapidreg.network;

import android.content.Context;

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
    public static final String API_BASE_URL = "http://10.29.3.184:3000";

    private static OkHttpClient.Builder httpClientBuilder = new OkHttpClient().newBuilder();
    private static Retrofit.Builder retrofitBuilder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    public static <S> S createService(Context context, Class<S> serviceClass) throws Exception {
        httpClientBuilder.sslSocketFactory(getSSLContext(context).getSocketFactory());
        httpClientBuilder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        Retrofit retrofit = retrofitBuilder.client(httpClientBuilder.build()).build();

        return retrofit.create(serviceClass);
    }

    private static SSLContext getSSLContext(Context context) throws Exception {
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
}
