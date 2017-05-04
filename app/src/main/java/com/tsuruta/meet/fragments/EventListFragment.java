package com.tsuruta.meet.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

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
    ArrayList<Event> events = new ArrayList<>();
    MainActivity parent;
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

        return llLayout;
    }

    private void getEvents()
    {
        //Network call to get data and save it to events arraylist
        getAllEventsFromFirebase();
    }

    private void setupRecycler()
    {
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

    public void getAllEventsFromFirebase()
    {
        FirebaseDatabase.getInstance()
                .getReference()
                .child(getString(R.string.db_events))
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
                            events.add(event);
                        }
                        //getEventCreatorNames();
                        setupRecycler();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {
                        // Unable to retrieve events.
                        Toast.makeText(faActivity.getApplicationContext(), "Unable to retrieve events", Toast.LENGTH_LONG).show();
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
