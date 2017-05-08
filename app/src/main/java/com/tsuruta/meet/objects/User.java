package com.tsuruta.meet.objects;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User
{
    public String uid;
    public String email;
    private ArrayList<String> events = new ArrayList<>();
    private String token;

    public User(){}

    public User(String uid, String email, String token)
    {
        this.uid = uid;
        this.email = email;
        this.token = token;
    }

    /*
    public void addEvent(String eventId)
    {
        this.events.add(eventId);
    }*/

    public String getEmail()
    {
        return email;
    }

    public String getUid()
    {
        return uid;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public ArrayList<String> getEvents()
    {
        return events;
    }

    @Exclude
    public Map<String, Object> toMap()
    {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email", email);
        result.put("events", events);
        result.put("token", token);

        return result;
    }
}
