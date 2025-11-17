package com.nukkadshops.mark02;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface voidService{
    @POST("api/void")
    Call<VoidResponse> uploadBilledTransaction(@Body VoidRequest request);
}
