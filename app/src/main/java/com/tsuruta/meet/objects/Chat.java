package com.tsuruta.meet.objects;

import android.widget.ProgressBar;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by michael on 5/1/17.
 */

public class Chat {
    public String sender;
    public String senderUid;
    public String eventUid;
    public String message;
    public long timestamp;

    public Chat(){}

    public Chat(String sender, String senderUid, String eventUid, String message, long timestamp)
    {
        this.sender = sender;
        this.senderUid = senderUid;
        this.eventUid = eventUid;
        this.message = message;
        this.timestamp = timestamp;
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
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("sender", sender);
        result.put("senderUid", senderUid);
        result.put("eventUid", eventUid);
        result.put("message", message);
        result.put("timestamp", timestamp);

        return result;
    }
}
