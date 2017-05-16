package com.tsuruta.meet.objects;

import android.graphics.Bitmap;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User
{
    public String uid;
    public String email;
    String name, avatar;
    private ArrayList<String> tokens;
    Bitmap bAvatar;

    public User(){}

    public User(String uid, String email, String avatar, String name)
    {
        this.uid = uid;
        this.email = email;
        this.avatar = avatar;
        this.name = name;
    }

    public User(String uid, Bitmap avatar)
    {
        this.uid = uid;
        this.bAvatar = avatar;
    }

    public String getEmail()
    {
        return email;
    }

    public String getUid()
    {
        return uid;
    }

    public String getName()
    {
        return name;
    }

    public String getAvatar()
    {
        return avatar;
    }

    public Bitmap getbAvatar()
    {
        return bAvatar;
    }

    public ArrayList<String> getToken()
    {
        return tokens;
    }

    public void addToken(String token)
    {
        this.tokens.add(token);
    }

    @Exclude
    public Map<String, Object> toMap()
    {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email", email);
        result.put("avatar", avatar);
        result.put("name", name);

        return result;
    }
}
