package com.eventhunt.retrofit;

import android.arch.lifecycle.MutableLiveData;

import com.eventhunt.entity.Event;
import com.eventhunt.entity.RequestBody;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class ApiCommunication {
    private static Retrofit retrofit;
    private static ApiCommunication communication;
    private static final String BASE_URL = "localhost";
    private static IApiCommunication apiCommunication;

    private ApiCommunication(){
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        apiCommunication = retrofit.create(IApiCommunication.class);
    }

    public static ApiCommunication getInstance() {
        if(communication == null){
            communication = new ApiCommunication();
        }
        return communication;
    }


    public MutableLiveData<List<Event>> getEvents(){
        MutableLiveData<List<Event>> liveData = new MutableLiveData<>();
        RequestBody requestBody = new RequestBody();
        apiCommunication.getEvents(requestBody).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
        return liveData;
    }

    public interface IApiCommunication{
        @POST("/get_events")
        Call<JsonObject> getEvents(@Body RequestBody requestBody);
    }

    static class Builder{
        private static Map<String, ApiCommunication> listInstance;

        public Builder(){
            if(listInstance == null){
                listInstance = new HashMap<>();
            }
        }

        public static ApiCommunication getInstance(String name){
            if(listInstance != null && listInstance.containsKey(name)){
                return listInstance.get(name);
            } else
                return null;
        }
    }
}
