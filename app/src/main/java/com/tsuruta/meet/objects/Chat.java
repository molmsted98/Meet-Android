package com.tsuruta.meet.objects;

/**
 * Created by michael on 5/1/17.
 */

public class Chat {
    public String sender;
    public String receiver;
    public String senderUid;
    public String receiverUid;
    public String message;
    public long timestamp;

    public Chat(){}

    public Chat(String sender, String receiver, String senderUid, String receiverUid, String message, long timestamp)
    {
        this.sender = sender;
        this.receiver = receiver;
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.message = message;
        this.timestamp = timestamp;
    }
}
