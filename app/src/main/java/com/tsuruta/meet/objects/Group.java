package com.tsuruta.meet.objects;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Group
{
    private String groupTitle, groupCreator, groupCreatorName;
    private boolean mPublic, mInvite, mJoined, invited;
    private String uid;
    private ArrayList<String> members = new ArrayList<>();
    private long timestamp;

    public Group(){}

    public Group(String groupTitle, String groupCreator, boolean mPublic,
                 boolean mInvite)
    {
        this.groupCreator = groupCreator;
        this.groupTitle = groupTitle;
        this.mPublic = mPublic;
        this.mInvite = mInvite;
    }

    public String getTitle()
    {
        return groupTitle;
    }

    public String getCreator()
    {
        return groupCreator;
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
        this.groupCreator = creator;
    }

    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    public void setTitle(String title)
    {
        this.groupTitle = title;
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
        this.groupCreatorName = name;
    }

    public String getCreatorName()
    {
        return groupCreatorName;
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
        result.put("title", groupTitle);
        result.put("creator", groupCreator);
        result.put("public", mPublic);
        result.put("invite", mInvite);

        return result;
    }
}
