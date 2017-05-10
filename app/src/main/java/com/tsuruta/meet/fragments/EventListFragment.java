package com.tsuruta.meet.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tsuruta.meet.recycler.EventRecyclerAdapter;
import com.tsuruta.meet.R;
import com.tsuruta.meet.activities.MainActivity;
import com.tsuruta.meet.objects.Event;

import java.util.ArrayList;
import java.util.Iterator;

public class EventListFragment extends Fragment {

    FragmentActivity faActivity;
    LinearLayout llLayout;
    ArrayList<Event> allEvents = new ArrayList<>();
    ArrayList<String> userEvents = new ArrayList<>();
    ArrayList<Event> events = new ArrayList<>();
    ArrayList<Event> publicEvents = new ArrayList<>();
    MainActivity parent;
    TextView tvNoEvents;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private EventRecyclerAdapter adapter;

    public static EventListFragment newInstance()
    {
        return new EventListFragment();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        getEvents();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        faActivity = super.getActivity();
        parent = (MainActivity)getActivity();
        llLayout = (LinearLayout)inflater.inflate(R.layout.fragment_eventlist, container, false);
        recyclerView = (RecyclerView)llLayout.findViewById(R.id.eventRecycler);
        tvNoEvents = (TextView)llLayout.findViewById(R.id.tvNoEvents);

        return llLayout;
    }

    private void getEvents()
    {
        //Network call to get data and save it to events arraylist
        getUsersEvents();
    }

    private void setupRecycler()
    {
        recyclerView.setVisibility(View.VISIBLE);
        tvNoEvents.setVisibility(View.GONE);
        recyclerView.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(faActivity);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new EventRecyclerAdapter(this, events);
        recyclerView.setAdapter(adapter);
    }

    public void eventClicked(int position)
    {
        faActivity.getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content_container, EventFragment.newInstance(events.get(position)), getString(R.string.fragment_event_name))
                .addToBackStack(getString(R.string.fragment_event_name))
                .commit();
    }

    public void joinEvent(int position)
    {
        final String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance()
                .getReference()
                .child(getString(R.string.db_events))
                .child(events.get(position).getUid())
                .child(getString(R.string.db_members))
                .child(currentUid)
                .setValue(true)
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            //Joined the event. Update the list now.
                            events.clear();
                            getUsersEvents();
                        }
                        else
                        {
                            // failed to add event
                            Toast.makeText(faActivity.getApplicationContext(), "Failed to join event", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void getUsersEvents()
    {
        final String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance()
                .getReference()
                .child(getString(R.string.db_events))
                .orderByChild(getString(R.string.db_members) + "/" + currentUid)
                .equalTo(true)
                .addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren()
                                .iterator();
                        while (dataSnapshots.hasNext())
                        {
                            DataSnapshot dataSnapshotChild = dataSnapshots.next();
                            Event event = dataSnapshotChild.getValue(Event.class);
                            event.setHasJoined(true);
                            event.setUid(dataSnapshotChild.getKey());
                            event.setTimestamp(Long.parseLong(dataSnapshotChild.child("timestamp").getValue().toString()));
                            events.add(event);
                        }
                        getPublicEvents();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {
                        // Unable to retrieve events.
                        Toast.makeText(faActivity.getApplicationContext(), "Unable to retrieve user events", Toast.LENGTH_LONG).show();
                    }
                });
    }

    //TODO: Only show these events if mutual friends or close in proximity
    public void getPublicEvents()
    {
        FirebaseDatabase.getInstance()
                .getReference()
                .child(getString(R.string.db_events))
                .orderByChild("public")
                .equalTo(true)
                .addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren()
                                .iterator();
                        while (dataSnapshots.hasNext())
                        {
                            DataSnapshot dataSnapshotChild = dataSnapshots.next();
                            Event event = dataSnapshotChild.getValue(Event.class);
                            event.setUid(dataSnapshotChild.getKey());
                            event.setHasJoined(false);
                            event.setTimestamp(Long.parseLong(dataSnapshotChild.child("timestamp").getValue().toString()));
                            boolean flag = false;
                            for(int i = 0; i < events.size(); i ++)
                            {
                                if(event.getUid().equals(events.get(i).getUid()))
                                {
                                    flag = true;
                                }
                            }
                            if(!flag)
                            {
                                events.add(event);
                            }
                        }
                        setupRecycler();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {
                        // Unable to retrieve events.
                        Toast.makeText(faActivity.getApplicationContext(), "Unable to retrieve all events", Toast.LENGTH_LONG).show();
                    }
                });
    }

    /*
    public void getEventCreatorNames()
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query queryRef = ref.orderByChild("uid").equalTo(creator);
        System.out.println("Setting up recycler view " + position);
        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                System.out.println("LOOK HERE: " + snapshot.getKey());
                User user = snapshot.getValue(User.class);
                creatorName = user.getName();
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

            }
        });

        setupRecycler();
    }*/
}
