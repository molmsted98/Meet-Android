package com.tsuruta.meet.objects;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User
{
    public String uid;
    public String email;
    public String firebaseToken;
    private ArrayList<String> events = new ArrayList<>();

    public User(){}

    public User(String uid, String email, String firebaseToken)
    {
        this.uid = uid;
        this.email = email;
        this.firebaseToken = firebaseToken;
    }

    public void addEvent(String eventId)
    {
        this.events.add(eventId);
    }

    public void setEvents(ArrayList<String> events)
    {
        this.events = events;
    }

    public String getEmail()
    {
        return email;
    }

    @Exclude
    public Map<String, Object> toMap()
    {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("email", email);
        result.put("events", events);
        result.put("token", firebaseToken);

        return result;
    }
}
