package org.unicef.rapidreg.network;

import org.unicef.rapidreg.model.LoginBody;
import org.unicef.rapidreg.model.Root;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkService {

    private static final String SERVER_BASE_URL = "http://10.29.5.149:3000";

    static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(SERVER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    static PrimeroApi service = retrofit.create(PrimeroApi.class);

    public static Root doLogin() {
        Call<Root> call = service.login(new LoginBody("15555215554", "qu01n23!", "primero", "android_id"));
        Root root = new Root();
        try {
            root = call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return root;
    }
}
