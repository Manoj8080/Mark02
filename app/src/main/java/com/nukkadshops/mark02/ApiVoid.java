package com.nukkadshops.mark02;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiVoid {
    private static Retrofit retrofit1;
    private static final String BASE_URL ="https://75df3dda161d.ngrok-free.app/";

    public static Retrofit getvoid(){
        if (retrofit1 == null){
            retrofit1 = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit1;
    }
}

