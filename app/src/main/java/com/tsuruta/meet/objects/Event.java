package com.tsuruta.meet.objects;

import com.google.firebase.database.Exclude;
import com.tsuruta.meet.objects.Chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by michael on 5/1/17.
 */

public class Event
{
    private String eventTitle, eventCreator, eventCreatorName;
    private long timestamp;
    private boolean mPublic, mInvite;
    private ArrayList<Chat> eventChats = new ArrayList<>();
    private String uid;

    public Event(){}

    public Event(String eventTitle, String eventCreator, long timestamp, boolean mPublic,
                 boolean mInvite, String uid)
    {
        this.eventCreator = eventCreator;
        this.eventTitle = eventTitle;
        this.timestamp = timestamp;
        this.mPublic = mPublic;
        this.mInvite = mInvite;
        this.uid = uid;
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
        return true;
    }

    public boolean getIsPublic()
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

    public void setAllowInvites(boolean b)
    {
        this.mInvite = b;
    }

    public void setIsPublic(boolean b)
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
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", eventTitle);
        result.put("creator", eventCreator);
        result.put("isPublic", mPublic);
        result.put("allowInvites", mInvite);
        result.put("timestamp", timestamp);
        result.put("uid", uid);

        return result;
    }

}
