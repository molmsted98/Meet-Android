package com.tsuruta.meet.objects;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User
{
    public String uid;
    public String email;
    private ArrayList<String> tokens;

    public User(){}

    public User(String uid, String email)
    {
        this.uid = uid;
        this.email = email;
    }

    public String getEmail()
    {
        return email;
    }

    public String getUid()
    {
        return uid;
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

        return result;
    }
}
