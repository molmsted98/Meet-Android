package com.tsuruta.meet.objects;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Chat {
    public String senderUid;
    public String groupUid;
    public String message;
    private String uid;

    public Chat(){}

    public Chat(String senderUid, String groupUid, String message, String uid)
    {
        this.senderUid = senderUid;
        this.groupUid = groupUid;
        this.message = message;
        this.uid = uid;
    }

    public Chat(String senderUid, String groupUid, String message)
    {
        this.senderUid = senderUid;
        this.groupUid = groupUid;
        this.message = message;
    }

    public String getGroupUid()
    {
        return groupUid;
    }

    public String getMessage()
    {
        return message;
    }

    public String getSenderUid()
    {
        return senderUid;
    }

    public String getUid()
    {
        return uid;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }

    @Exclude
    public Map<String, Object> toMap()
    {
        HashMap<String, Object> result = new HashMap<>();

        result.put("senderUid", senderUid);
        result.put("message", message);

        return result;
    }
}
