package com.tsuruta.meet.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tsuruta.meet.R;
import com.tsuruta.meet.activities.MainActivity;
import com.tsuruta.meet.objects.Chat;
import com.tsuruta.meet.objects.Event;
import com.tsuruta.meet.recycler.ChatRecyclerAdapter;

import java.util.ArrayList;

import static com.google.android.gms.internal.zzt.TAG;

public class EventFragment extends Fragment implements View.OnClickListener, View.OnFocusChangeListener
{
    FragmentActivity faActivity;
    LinearLayout llLayout;
    MainActivity parent;
    ImageView ivSendMessage;
    EditText etMessage;
    private FirebaseAuth mAuth;
    Event event;
    ArrayList<Chat> chats = new ArrayList<>();
    RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ChatRecyclerAdapter adapter;
    boolean firstTime;

    public static EventFragment newInstance(Event event)
    {
        EventFragment ef = new EventFragment();
        ef.event = event;
        return ef;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        firstTime = true;
        mAuth = FirebaseAuth.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        faActivity = super.getActivity();
        parent = (MainActivity) getActivity();
        llLayout = (LinearLayout) inflater.inflate(R.layout.fragment_event, container, false);
        ivSendMessage = (ImageView) llLayout.findViewById(R.id.sendButton);
        etMessage = (EditText) llLayout.findViewById(R.id.messageArea);
        etMessage.setOnFocusChangeListener(this);
        ivSendMessage.setOnClickListener(this);
        recyclerView = (RecyclerView) llLayout.findViewById(R.id.chatRecycler);
        recyclerView.setOnClickListener(this);

        //TODO: Update actionbar title with name of the event
        String eventName = event.getTitle();

        //TODO: Remove the plus button from the action bar

        //TODO: Triple check that the user is logged in before allowing them to see the chat
        //Also maybe check to see that they're in the event? Can't hurt to do some verification

        getAllChats();

        return llLayout;
    }

    @Override
    public void onClick(View view)
    {
        if (view == ivSendMessage)
        {
            String message = etMessage.getText().toString();
            Chat newChat = new Chat("Sender Name", mAuth.getCurrentUser().getUid(), event.getUid(), message, System.currentTimeMillis());
            sendMessageToFirebaseEvent(parent.getApplicationContext(), newChat);
            etMessage.setText("");
        }
    }

    @Override
    public void onFocusChange(View view, boolean b)
    {
        if(view == etMessage)
        {
            if(!b)
            {
                hideKeyboard(view);
            }
        }
    }

    public void sendMessageToFirebaseEvent(final Context context, final Chat chat)
    {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        Log.e(TAG, "sendMessageToFirebaseEvent: success");
        databaseReference.child("chats")
                .child(chat.getEventUid())
                .child(String.valueOf(chat.timestamp))
                .setValue(chat.toMap())
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            //TODO: Show cute lil' sent icon
                        }
                        else
                        {
                            //TODO: Allow user to retry sending
                            Toast.makeText(context, "Failed to send message", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void getAllChats()
    {
        FirebaseDatabase.getInstance()
                .getReference()
                .child("chats")
                .child(event.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        FirebaseDatabase.getInstance()
                                .getReference()
                                .child("chats")
                                .child(event.getUid())
                                .addChildEventListener(new ChildEventListener()
                                {
                                    @Override
                                    public void onChildAdded(DataSnapshot dataSnapshot, String s)
                                    {
                                        Chat chat = dataSnapshot.getValue(Chat.class);
                                        chats.add(chat);

                                        if (firstTime)
                                        {
                                            firstTime = false;
                                            setupRecycler();
                                        }
                                        else
                                        {
                                            recyclerView.scrollToPosition(chats.size() - 1);
                                            adapter.updateList(chats);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError)
                                    {
                                        // Unable to get message.
                                        Toast.makeText(faActivity.getApplicationContext(), "Unable to retrieve new chat", Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onChildChanged(DataSnapshot dataSnapshot, String s){}

                                    @Override
                                    public void onChildRemoved(DataSnapshot dataSnapshot){}

                                    @Override
                                    public void onChildMoved(DataSnapshot dataSnapshot, String s){}
                                });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {
                        // Unable to retrieve chats.
                        Toast.makeText(faActivity.getApplicationContext(), "Unable to retrieve chats", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void setupRecycler()
    {
        recyclerView.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(faActivity);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ChatRecyclerAdapter(this, chats);
        recyclerView.setAdapter(adapter);
    }

    public void hideKeyboard(View view)
    {
        InputMethodManager inputMethodManager =(InputMethodManager)parent.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}