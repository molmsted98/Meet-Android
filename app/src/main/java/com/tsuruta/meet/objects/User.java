package com.tsuruta.meet.objects;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User
{
    public String uid;
    public String phoneNum;
    public String firebaseToken;
    private ArrayList<String> events = new ArrayList<>();

    public User(){}

    public User(String uid, String phoneNum, String firebaseToken)
    {
        this.uid = uid;
        this.phoneNum = phoneNum;
        this.firebaseToken = firebaseToken;
    }

    public void addEvent(String eventId)
    {
        events.add(eventId);
    }

    public String getName()
    {
        return phoneNum;
    }

    @Exclude
    public Map<String, Object> toMap()
    {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("phoneNum", phoneNum);
        result.put("events", events);

        return result;
    }
}
