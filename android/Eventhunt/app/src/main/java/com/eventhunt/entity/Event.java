package com.eventhunt.entity;

import android.location.Address;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.Date;

public class Event implements Parcelable{
    private String title;
    private Address address;
    private String description;
    private Calendar startEvent;

    public Event(){

    }

    protected Event(Parcel in) {
        title = in.readString();
        address = in.readParcelable(Address.class.getClassLoader());
        description = in.readString();
        startEvent = Calendar.getInstance();
        startEvent.setTime(new Date(in.readLong()));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
    }

    @Override
    public String toString() {
        String theDate = startEvent.get(Calendar.HOUR_OF_DAY) + ":" + startEvent.get(Calendar.MINUTE) + ":00 "
                + startEvent.get(Calendar.DAY_OF_MONTH) + "." + startEvent.get(Calendar.MONTH) + "." + startEvent.get(Calendar.YEAR);
        return "Event{" +
                "title='" + title + '\'' +
                ", address=" + address +
                ", description='" + description + '\'' +
                ", startEvent=" + theDate +
                '}';
    }
}
