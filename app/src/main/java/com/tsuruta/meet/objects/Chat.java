package com.tsuruta.meet.objects;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Chat {
    public String senderUid;
    public String eventUid;
    public String message;
    private String uid;

    public Chat(){}

    public Chat(String senderUid, String eventUid, String message, String uid)
    {
        this.senderUid = senderUid;
        this.eventUid = eventUid;
        this.message = message;
        this.uid = uid;
    }

    public Chat(String senderUid, String eventUid, String message)
    {
        this.senderUid = senderUid;
        this.eventUid = eventUid;
        this.message = message;
    }

    public String getEventUid()
    {
        return eventUid;
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
