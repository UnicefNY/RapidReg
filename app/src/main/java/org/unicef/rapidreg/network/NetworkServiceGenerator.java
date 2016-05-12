package org.unicef.rapidreg.network;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkServiceGenerator {

//    public static final String API_BASE_URL = "http://10.29.5.149:3000";
    public static final String API_BASE_URL = "http://10.29.3.184:3000";

    private static OkHttpClient.Builder httpClient = new OkHttpClient().newBuilder();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    public static <S> S createService(Class<S> serviceClass) {
        Retrofit retrofit = builder.client(httpClient.build()).build();
        return retrofit.create(serviceClass);
    }

}
