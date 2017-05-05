package com.tsuruta.meet.firebase;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NotificationBuilder
{
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String TAG = "NotificationBuilder";
    private static final String SERVER_API_KEY = "AAAAt-bqmVc:APA91bFxd3DRHMXR5Myww2Nay5nk6nmxrS4iNjAryiR-Fe3YM_r3ph5nSnVwAiVbk_LBAewrHDukc_PLzK7Dqh3WgDFAlkEYuDKYPSiFewHV0Dm3i7aGI_f0vs0VH6Xdx88qHJsqitfa";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String AUTHORIZATION = "Authorization";
    private static final String AUTH_KEY = "key=" + SERVER_API_KEY;
    private static final String FCM_URL = "https://fcm.googleapis.com/fcm/send";
    // json related keys
    private static final String KEY_TO = "to";
    private static final String KEY_NOTIFICATION = "notification";
    private static final String KEY_TITLE = "title";
    private static final String KEY_TEXT = "text";
    private static final String KEY_DATA = "data";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_UID = "uid";
    private static final String KEY_FCM_TOKEN = "fcm_token";

    private String mTitle;
    private String mMessage;
    private String mUsername;
    private String mUid;
    private String mFirebaseToken;
    private ArrayList<String> mReceiverFirebaseTokens;

    private NotificationBuilder() {

    }

    public static NotificationBuilder initialize() {
        return new NotificationBuilder();
    }

    public NotificationBuilder title(String title) {
        mTitle = title;
        return this;
    }

    public NotificationBuilder message(String message) {
        mMessage = message;
        return this;
    }

    public NotificationBuilder username(String username) {
        mUsername = username;
        return this;
    }

    public NotificationBuilder uid(String uid) {
        mUid = uid;
        return this;
    }

    public NotificationBuilder firebaseToken(String firebaseToken) {
        mFirebaseToken = firebaseToken;
        return this;
    }

    public NotificationBuilder receiverFirebaseTokens(ArrayList<String> receiverFirebaseTokens) {
        mReceiverFirebaseTokens = receiverFirebaseTokens;
        return this;
    }

    public void send() {
        for(int i = 0; i < mReceiverFirebaseTokens.size(); i ++)
        {
            RequestBody requestBody = null;
            try {
                requestBody = RequestBody.create(MEDIA_TYPE_JSON, getValidJsonBody(i).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Request request = new Request.Builder()
                    .addHeader(CONTENT_TYPE, APPLICATION_JSON)
                    .addHeader(AUTHORIZATION, AUTH_KEY)
                    .url(FCM_URL)
                    .post(requestBody)
                    .build();

            Call call = new OkHttpClient().newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "onGetAllUsersFailure: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.e(TAG, "onResponse: " + response.body().string());
                }
            });
        }
    }

    private JSONObject getValidJsonBody(int i) throws JSONException {
        JSONObject jsonObjectBody = new JSONObject();
        jsonObjectBody.put(KEY_TO, mReceiverFirebaseTokens.get(i));

        JSONObject jsonObjectData = new JSONObject();
        jsonObjectData.put(KEY_TITLE, mTitle);
        jsonObjectData.put(KEY_TEXT, mMessage);
        jsonObjectData.put(KEY_USERNAME, mUsername);
        jsonObjectData.put(KEY_UID, mUid);
        jsonObjectData.put(KEY_FCM_TOKEN, mFirebaseToken);
        jsonObjectBody.put(KEY_DATA, jsonObjectData);

        return jsonObjectBody;
    }
}
