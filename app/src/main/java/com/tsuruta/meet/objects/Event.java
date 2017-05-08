package com.tsuruta.meet.objects;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Event
{
    private String eventTitle, eventCreator, eventCreatorName;
    private long timestamp;
    private boolean mPublic, mInvite, mJoined;
    private ArrayList<String> members;
    private String uid;

    public Event(){}

    public Event(String eventTitle, String eventCreator, long timestamp, boolean mPublic,
                 boolean mInvite)
    {
        this.eventCreator = eventCreator;
        this.eventTitle = eventTitle;
        this.timestamp = timestamp;
        this.mPublic = mPublic;
        this.mInvite = mInvite;
    }

    public String getTitle()
    {
        return eventTitle;
    }

    public String getCreator()
    {
        return eventCreator;
    }

    //Look to see if the current user is in the userlist
    public boolean getHasJoined()
    {
        return mJoined;
    }

    public void setHasJoined(boolean a)
    {
        this.mJoined = a;
    }

    public boolean getPublic()
    {
        return mPublic;
    }

    public boolean getInvite()
    {
        return mInvite;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public void setCreator(String creator)
    {
        this.eventCreator = creator;
    }

    public void setTitle(String title)
    {
        this.eventTitle = title;
    }

    public void setInvite(boolean b)
    {
        this.mInvite = b;
    }

    public void setPublic(boolean b)
    {
        this.mPublic = b;
    }

    public void setEventCreatorName(String name)
    {
        this.eventCreatorName = name;
    }

    public String getEventCreatorName()
    {
        return eventCreatorName;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }

    public String getUid()
    {
        return uid;
    }

    @Exclude
    public Map<String, Object> toMap()
    {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", eventTitle);
        result.put("creator", eventCreator);
        result.put("public", mPublic);
        result.put("invite", mInvite);
        result.put("timestamp", timestamp);

        return result;
    }
}
