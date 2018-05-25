package com.eventhunt.entity;

import com.google.gson.annotations.SerializedName;

public class RequestBody {
    @SerializedName("token")
    String token;
    @SerializedName("category")
    String typeEvent;
    @SerializedName("subcategory")
    String subCategory;
    @SerializedName("cost")
    int cost;
    @SerializedName("eventDate")
    String eventDate;
    @SerializedName("longitude")
    int longitude;
    @SerializedName("latitude")
    int latitude;
    @SerializedName("userDate")
    String userDate;

    public RequestBody(String token, String typeEvent, String subCategory, int cost, String eventDate, int longitude, int latitude, String userDate) {
        this.token = token;
        this.typeEvent = typeEvent;
        this.subCategory = subCategory;
        this.cost = cost;
        this.eventDate = eventDate;
        this.longitude = longitude;
        this.latitude = latitude;
        this.userDate = userDate;
    }

    public RequestBody(){

    }
}
