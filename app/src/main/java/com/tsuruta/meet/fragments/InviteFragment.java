package com.tsuruta.meet.fragments;

import android.content.Intent;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.tsuruta.meet.R;
import com.tsuruta.meet.activities.MainActivity;
import com.tsuruta.meet.objects.User;
import com.tsuruta.meet.recycler.UserRecyclerAdapter;

import java.util.ArrayList;


public class InviteFragment extends Fragment implements View.OnClickListener
{
    FragmentActivity faActivity;
    LinearLayout llLayout;
    MainActivity parent;
    ImageView ivUserSelect;
    RecyclerView recyclerView;
    Button btnAddUsers;
    ArrayList<User> users = new ArrayList<>();
    ArrayList<String> inviteUsers = new ArrayList<>();
    String eventUid;
    private RecyclerView.LayoutManager layoutManager;
    private UserRecyclerAdapter adapter;

    public static InviteFragment newInstance(String uid)
    {
        InviteFragment newIf = new InviteFragment();
        newIf.eventUid = uid;
        return newIf;
    }

    public void userClicked(int position, boolean selected, String uid)
    {
        if(selected)
        {
            inviteUsers.add(uid);
        }
        else
        {
            inviteUsers.remove(uid);
        }
        System.out.println("Size of users " + inviteUsers.size());
        if(inviteUsers.size() == 0)
        {
            btnAddUsers.setEnabled(false);
        }
        else
        {
            btnAddUsers.setEnabled(true);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        faActivity = super.getActivity();
        parent = (MainActivity) getActivity();
        llLayout = (LinearLayout) inflater.inflate(R.layout.fragment_invite, container, false);
        recyclerView = (RecyclerView) llLayout.findViewById(R.id.userInviteRecycler);
        ivUserSelect = (ImageView) llLayout.findViewById(R.id.ivUserSelect);
        btnAddUsers = (Button) llLayout.findViewById(R.id.btnInviteUsers);
        btnAddUsers.setOnClickListener(this);

        //TODO: Update actionbar title with name of the event

        //TODO: Remove the plus button from the action bar

        //TODO: Get the list of users right here
        //Make sure that they're not already in the event
        users.add(new User("44Kq73aF9ONK2nuoarASgXZ9h9h2", "molmsted98@gmail.com"));
        users.add(new User("IVXKlLGQY8V9cah9AwnLqD2PQ9h2", "zachorr139@gmail.com"));

        setupRecycler();

        return llLayout;
    }

    @Override
    public void onClick(View view)
    {
        if(view == btnAddUsers)
        {
            for(int i = 0; i < inviteUsers.size(); i ++)
            {
                FirebaseDatabase.getInstance()
                        .getReference()
                        .child(getString(R.string.db_events))
                        .child(eventUid)
                        .child(getString(R.string.db_members))
                        .child(inviteUsers.get(i))
                        .setValue(false);
            }

            faActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content_container, EventListFragment.newInstance(), "eventList")
                    .commit();
        }
    }

    private void setupRecycler()
    {
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(faActivity);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new UserRecyclerAdapter(this, users);
        recyclerView.setAdapter(adapter);
    }
}
