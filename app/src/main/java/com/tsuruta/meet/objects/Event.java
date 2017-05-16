package com.tsuruta.meet.objects;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Event
{
    private String eventTitle, eventCreator, eventCreatorName;
    private boolean mPublic, mInvite, mJoined, invited;
    private String uid;
    private ArrayList<String> members = new ArrayList<>();
    private long timestamp;

    public Event(){}

    public Event(String eventTitle, String eventCreator, boolean mPublic,
                 boolean mInvite)
    {
        this.eventCreator = eventCreator;
        this.eventTitle = eventTitle;
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

    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
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

    public void setCreatorName(String name)
    {
        this.eventCreatorName = name;
    }

    public String getCreatorName()
    {
        return eventCreatorName;
    }

    public void setTheMembers(ArrayList<String> memberUids)
    {
        members = memberUids;
    }

    public ArrayList<String> getTheMembers()
    {
        return members;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }

    public String getUid()
    {
        return uid;
    }

    public void setInvited(boolean invited)
    {
        this.invited = invited;
    }

    public boolean getInvited()
    {
        return invited;
    }

    @Exclude
    public Map<String, Object> toMap()
    {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", eventTitle);
        result.put("creator", eventCreator);
        result.put("public", mPublic);
        result.put("invite", mInvite);

        return result;
    }
}
