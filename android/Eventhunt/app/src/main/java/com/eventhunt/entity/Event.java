package com.eventhunt.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.location.Address;
import android.os.Parcel;
import android.os.Parcelable;

import com.eventhunt.converter.AddressConverter;
import com.eventhunt.converter.CalendarConverter;
import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

// TODO create field for owner
@Entity
public class Event implements Parcelable{
    @PrimaryKey
    public int id;
    private String title;
    @TypeConverters(AddressConverter.class)
    private Address address;
    private String description;
    @TypeConverters(CalendarConverter.class)
    private Calendar startEvent;
    private String type;
    private double cost;
    @Ignore
    private int idMarker;
    private static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    public Event(){

    }

    public int getIdMarker() {
        return idMarker;
    }

    public void setIdMarker(int idMarker) {
        this.idMarker = idMarker;
    }

    public static String convertDateToString(Date date){
        return sdf.format(date);
    }

    public static Date convertStringToDate(String date) throws ParseException {
        return sdf.parse(date);
    }

    protected Event(Parcel in) {
        title = in.readString();
        address = in.readParcelable(Address.class.getClassLoader());
        description = in.readString();
        startEvent = Calendar.getInstance();
        startEvent.setTime(new Date(in.readLong()));
        type = in.readString();
        cost = in.readDouble();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public void setType(String type, String genre) {
        this.type = type + "/" + genre;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LatLng getPosition(){
        return new LatLng(address.getLatitude(), address.getLongitude());
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Calendar getStartEvent() {
        return startEvent;
    }

    public void setStartEvent(Calendar startEvent) {
        this.startEvent = startEvent;
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeParcelable(address, flags);
        dest.writeString(description);
        dest.writeLong(startEvent.getTime().getTime());
        dest.writeString(type);
        dest.writeDouble(cost);
    }

    @Override
    public String toString() {
        String theDate = convertDateToString(startEvent.getTime());
        return "Event{" +
                "title='" + title + '\'' +
                ", type='" + type + "\'" +
                ", address=" + address +
                ", description='" + description + '\'' +
                ", cost='" + cost + '\'' +
                ", startEvent=" + theDate +
                '}';
    }
}
