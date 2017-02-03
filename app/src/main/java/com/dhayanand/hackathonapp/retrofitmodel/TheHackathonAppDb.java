package com.dhayanand.hackathonapp.retrofitmodel;

import com.dhayanand.hackathonapp.retrofitmodel.model.Raw;

import retrofit.Call;
import retrofit.Converter;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.http.GET;

/**
 * Created by Dhayanand on 1/14/2017.
 */

public class TheHackathonAppDb {

    private static TheHackathonAppDbApiInterface service;

    public interface TheHackathonAppDbApiInterface {
        @GET("/android/hackathonApp/json.txt")
        Call<Raw> getHackathonList();
    }

    public static TheHackathonAppDbApiInterface getApiClient() {
        if (service != null)
            return service;

        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://dhayanand.com")
                .addConverterFactory((Converter.Factory) GsonConverterFactory.create()).build();
        service = retrofit.create(TheHackathonAppDbApiInterface.class);

        return service;
    }
}
