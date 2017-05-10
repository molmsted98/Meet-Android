package com.tsuruta.meet.objects;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Chat {
    public String sender;
    public String senderUid;
    public String eventUid;
    public String message;

    public Chat(){}

    public Chat(String sender, String senderUid, String eventUid, String message)
    {
        this.sender = sender;
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

    @Exclude
    public Map<String, Object> toMap()
    {
        HashMap<String, Object> result = new HashMap<>();

        result.put("sender", sender);
        result.put("senderUid", senderUid);
        result.put("message", message);

        return result;
    }
}
