package com.tsuruta.meet.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tsuruta.meet.R;
import com.tsuruta.meet.activities.MainActivity;
import com.tsuruta.meet.objects.Chat;

import static com.google.android.gms.internal.zzt.TAG;

/**
 * Created by michael on 5/1/17.
 */

public class EventFragment extends Fragment implements View.OnClickListener
{
    FragmentActivity faActivity;
    LinearLayout llLayout;
    MainActivity parent;
    ImageView ivSendMessage;
    EditText etMessage;
    private FirebaseAuth mAuth;

    public static EventFragment newInstance()
    {
        return new EventFragment();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        faActivity = super.getActivity();
        parent = (MainActivity)getActivity();
        llLayout = (LinearLayout)inflater.inflate(R.layout.fragment_event, container, false);
        ivSendMessage = (ImageView) llLayout.findViewById(R.id.sendButton);
        etMessage = (EditText) llLayout.findViewById(R.id.messageArea);
        ivSendMessage.setOnClickListener(this);

        return llLayout;
    }

    @Override
    public void onClick(View view)
    {
        if(view == ivSendMessage)
        {
            FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
            mUser.getToken(true)
                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if (task.isSuccessful()) {
                                String idToken = task.getResult().getToken();

                                //Chat newChat = new Chat(mAuth.getCurrentUser().getUid(), System.currentTimeMillis());
                                //sendMessageToFirebaseEvent(parent.getApplicationContext(), newChat, idToken);
                            } else {
                                // Handle error -> task.getException();
                                Log.e(TAG, "onClick-SendMessage: Couldn't get user token.");
                            }
                        }
                    });
        }
    }

    public void sendMessageToFirebaseEvent(final Context context,
                                          final Chat chat,
                                          final String receiverFirebaseToken) {
        final String room_type_1 = chat.senderUid + "_" + chat.receiverUid;
        final String room_type_2 = chat.receiverUid + "_" + chat.senderUid;

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference();

        databaseReference.child("chats")
                .getRef()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(room_type_1)) {
                            Log.e(TAG, "sendMessageToFirebaseEvent: " + room_type_1 + " exists");
                            databaseReference.child("events")
                                    .child(room_type_1)
                                    .child(String.valueOf(chat.timestamp))
                                    .setValue(chat);
                        } else if (dataSnapshot.hasChild(room_type_2)) {
                            Log.e(TAG, "sendMessageToFirebaseEvent: " + room_type_2 + " exists");
                            databaseReference.child("events")
                                    .child(room_type_2)
                                    .child(String.valueOf(chat.timestamp))
                                    .setValue(chat);
                        } else {
                            Log.e(TAG, "sendMessageToFirebaseEvent: success");
                            databaseReference.child("events")
                                    .child(room_type_1)
                                    .child(String.valueOf(chat.timestamp))
                                    .setValue(chat);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Unable to send message.
                        //Toast.makeText(getFragmentManager().getClass(), "Message not sent");
                    }
                });
    }


    public void getMessageFromFirebaseEvent(String senderUid, String receiverUid) {
        final String room_type_1 = senderUid + "_" + receiverUid;
        final String room_type_2 = receiverUid + "_" + senderUid;

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference();

        databaseReference.child("events")
                .getRef()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(room_type_1)) {
                            Log.e(TAG, "getMessageFromFirebaseEvent: " + room_type_1 + " exists");
                            FirebaseDatabase.getInstance()
                                    .getReference()
                                    .child("events")
                                    .child(room_type_1)
                                    .addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                            // Chat message is retreived.
                                            Chat chat = dataSnapshot.getValue(Chat.class);
                                        }

                                        @Override
                                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                                        }

                                        @Override
                                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            // Unable to get message.
                                        }
                                    });
                        } else if (dataSnapshot.hasChild(room_type_2)) {
                            Log.e(TAG, "getMessageFromFirebaseEvent: " + room_type_2 + " exists");
                            FirebaseDatabase.getInstance()
                                    .getReference()
                                    .child("events")
                                    .child(room_type_2)
                                    .addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                            // Chat message is retreived.
                                            Chat chat = dataSnapshot.getValue(Chat.class);
                                        }

                                        @Override
                                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                                        }

                                        @Override
                                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            // Unable to get message.
                                        }
                                    });
                        } else {
                            Log.e(TAG, "getMessageFromFirebaseEvent: no such room available");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Unable to get message
                    }
                });
    }
}
