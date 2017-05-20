package com.tsuruta.meet.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
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
import com.tsuruta.meet.objects.User;
import com.tsuruta.meet.recycler.GroupRecyclerAdapter;
import com.tsuruta.meet.R;
import com.tsuruta.meet.activities.MainActivity;
import com.tsuruta.meet.objects.Group;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

public class GroupListFragment extends Fragment
{
    FragmentActivity faActivity;
    LinearLayout llLayout;
    LinearLayout llGroupList;
    ArrayList<Group> groups = new ArrayList<>();
    ArrayList<ArrayList<String>> urlList = new ArrayList<>();
    MainActivity parent;
    TextView tvNoGroups;
    View mProgressView;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private GroupRecyclerAdapter adapter;
    String dbGroups, dbMembers, dbUsers;
    int shortAnimTime;

    public static GroupListFragment newInstance()
    {
        return new GroupListFragment();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        groups.clear();
        getInviteGroups();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        faActivity = super.getActivity();
        parent = (MainActivity)getActivity();
        llLayout = (LinearLayout)inflater.inflate(R.layout.fragment_grouplist, container, false);
        llGroupList = (LinearLayout)llLayout.findViewById(R.id.llGroupList);
        mProgressView = llLayout.findViewById(R.id.group_progress);
        recyclerView = (RecyclerView)llLayout.findViewById(R.id.groupRecycler);
        tvNoGroups = (TextView)llLayout.findViewById(R.id.tvNoGroups);
        parent.setAddVisibility(true);
        parent.setBottomNavigationViewVisibility(true);
        showProgress(true);
        shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        //Get database constants
        dbGroups = getString(R.string.db_groups);
        dbMembers = getString(R.string.db_members);
        dbUsers = getString(R.string.db_users);

        cacheUserData();

        return llLayout;
    }

    private void setupRecycler()
    {
        if(groups.size() == 0)
        {
            recyclerView.setVisibility(View.GONE);
            tvNoGroups.setVisibility(View.VISIBLE);
        }
        else
        {
            recyclerView.setVisibility(View.VISIBLE);
            tvNoGroups.setVisibility(View.GONE);
        }
        recyclerView.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(faActivity);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new GroupRecyclerAdapter(this, groups, urlList);
        recyclerView.setAdapter(adapter);
        showProgress(false);
    }

    public void groupClicked(int position)
    {
        faActivity.getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content_container, GroupFragment.newInstance(groups.get(position)), getString(R.string.fragment_group_name))
                .addToBackStack(getString(R.string.fragment_group_name))
                .commit();
    }

    public void joinGroup(int position)
    {
        final String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance()
                .getReference()
                .child(dbGroups)
                .child(groups.get(position).getUid())
                .child(dbMembers)
                .child(currentUid)
                .setValue(true)
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            //Joined the group. Update the list now.
                            groups.clear();
                            getInviteGroups();
                        }
                        else
                        {
                            // failed to add group
                            Toast.makeText(faActivity.getApplicationContext(), "Failed to join group", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void acceptInvite(int position)
    {
        final String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance()
                .getReference()
                .child(dbGroups)
                .child(groups.get(position).getUid())
                .child(dbMembers)
                .child(currentUid)
                .setValue(true)
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            //Joined the group. Update the list now.
                            groups.clear();
                            getInviteGroups();
                        }
                        else
                        {
                            // failed to add group
                            Toast.makeText(faActivity.getApplicationContext(), "Failed to accept invite", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void denyInvite(int position)
    {
        final String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance()
                .getReference()
                .child(dbGroups)
                .child(groups.get(position).getUid())
                .child(dbMembers)
                .child(currentUid)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            //Joined the group. Update the list now.
                            groups.clear();
                            getInviteGroups();
                        }
                        else
                        {
                            // failed to add group
                            Toast.makeText(faActivity.getApplicationContext(), "Failed to deny invite", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void getInviteGroups()
    {
        final String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance()
                .getReference()
                .child(dbGroups)
                .orderByChild(dbMembers + "/" + currentUid)
                .equalTo(false)
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
                            Group group = dataSnapshotChild.getValue(Group.class);
                            group.setUid(dataSnapshotChild.getKey());
                            group.setInvited(true);
                            group.setHasJoined(false);
                            if(dataSnapshotChild.child("timestamp").getValue() != null)
                            {
                                group.setTimestamp(Long.parseLong(dataSnapshotChild.child("timestamp").getValue().toString()));
                            }
                            boolean flag = false;
                            group.setTheMembers(getGroupMembers(dataSnapshotChild));

                            for(int i = 0; i < groups.size(); i ++)
                            {
                                if(group.getUid().equals(groups.get(i).getUid()))
                                {
                                    flag = true;
                                }
                            }
                            if(!flag)
                            {
                                groups.add(group);
                            }
                        }
                        getUsersGroups();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {
                        // Unable to retrieve groups.
                        Toast.makeText(faActivity.getApplicationContext(), "Unable to retrieve invite groups", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void getUsersGroups()
    {
        final String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance()
                .getReference()
                .child(dbGroups)
                .orderByChild(dbMembers + "/" + currentUid)
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
                            Group group = dataSnapshotChild.getValue(Group.class);
                            group.setHasJoined(true);
                            group.setUid(dataSnapshotChild.getKey());
                            if(dataSnapshotChild.child("timestamp").getValue() != null)
                            {
                                group.setTimestamp(Long.parseLong(dataSnapshotChild.child("timestamp").getValue().toString()));
                            }
                            group.setTheMembers(getGroupMembers(dataSnapshotChild));
                            groups.add(group);
                        }
                        getPublicGroups();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {
                        // Unable to retrieve groups.
                        Toast.makeText(faActivity.getApplicationContext(), "Unable to retrieve user groups", Toast.LENGTH_LONG).show();
                    }
                });
    }

    //TODO: Only show these groups if mutual friends or close in proximity
    public void getPublicGroups()
    {
        FirebaseDatabase.getInstance()
                .getReference()
                .child(dbGroups)
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
                            Group group = dataSnapshotChild.getValue(Group.class);
                            group.setUid(dataSnapshotChild.getKey());
                            group.setHasJoined(false);
                            if(dataSnapshotChild.child("timestamp").getValue() != null)
                            {
                                group.setTimestamp(Long.parseLong(dataSnapshotChild.child("timestamp").getValue().toString()));
                            }
                            group.setTheMembers(getGroupMembers(dataSnapshotChild));
                            boolean flag = false;
                            for(int i = 0; i < groups.size(); i ++)
                            {
                                if(group.getUid().equals(groups.get(i).getUid()))
                                {
                                    flag = true;
                                }
                            }
                            if(!flag)
                            {
                                groups.add(group);
                            }
                        }
                        prepGroupUserData();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {
                        // Unable to retrieve groups.
                        Toast.makeText(faActivity.getApplicationContext(), "Unable to retrieve public groups", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void prepGroupUserData()
    {
        //Get group creators names
        for(int i = 0; i < groups.size(); i ++)
        {
            String[] creatorData = getSavedUserData(groups.get(i).getCreator());
            groups.get(i).setCreatorName(creatorData[0]);
            //Get group members imageUrls
            urlList.add(new ArrayList<String>());
            for(int j = 0; j < groups.get(i).getTheMembers().size(); j++)
            {
                String[] memberData = getSavedUserData(groups.get(i).getTheMembers().get(j));
                urlList.get(i).add(memberData[1]);
            }
        }
        setupRecycler();
    }

    private void cacheUserData()
    {
        try
        {
            File f = new File(parent.getString(R.string.USERS_FILENAME));
            f.delete();
            final FileOutputStream fos = parent.openFileOutput(parent.getString(R.string.USERS_FILENAME), parent.getApplicationContext().MODE_PRIVATE);

            //TODO: Only run this query for users in your groups
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child(getString(R.string.db_users))
                    .addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
                            while (dataSnapshots.hasNext())
                            {
                                DataSnapshot dataSnapshotChild = dataSnapshots.next();
                                User user = dataSnapshotChild.getValue(User.class);
                                String toWrite = dataSnapshotChild.getKey() + "\n" + user.getName() + "\n" + user.getAvatar() + "\n" + "\n";
                                try
                                {
                                    fos.write(toWrite.getBytes());
                                }
                                catch(IOException ex)
                                {
                                    System.out.println("ERROR " + ex);
                                }
                            }
                            try
                            {
                                fos.close();
                            }
                            catch (IOException ex)
                            {
                                System.out.println("Error " + ex);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError)
                        {
                            // Unable to retrieve groups.
                            Toast.makeText(faActivity.getApplicationContext(), "Unable to retrieve users", Toast.LENGTH_LONG).show();
                        }
                    });
        }
        catch (FileNotFoundException ex)
        {
            System.out.println("ERROR " + ex);
        }
    }

    private ArrayList<String> getGroupMembers(DataSnapshot dataSnapshotChild)
    {
        ArrayList<String> memberUids = new ArrayList<>();

        //Save member uids
        Iterator<DataSnapshot> groupProperties = dataSnapshotChild.getChildren().iterator();
        DataSnapshot members = null;
        while (groupProperties.hasNext())
        {
            DataSnapshot property = groupProperties.next();
            if(property.getKey().equals(dbMembers))
            {
                members = property;
            }
        }
        if(members != null)
        {
            Iterator<DataSnapshot> groupMembers = members.getChildren().iterator();
            while(groupMembers.hasNext())
            {
                DataSnapshot potentialMember = groupMembers.next();
                if((boolean)potentialMember.getValue())
                {
                    memberUids.add(potentialMember.getKey());
                }
            }
        }
        return memberUids;
    }

    private String[] getSavedUserData(String uid)
    {
        String[] data = new String[2];
        try
        {
            FileInputStream fis = parent.getApplicationContext().openFileInput(parent.getString(R.string.USERS_FILENAME));
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if(line.equals(uid))
                {
                    data[0] = bufferedReader.readLine();
                    data[1] = bufferedReader.readLine();
                    break;
                }
            }
            return data;
        }
        catch (IOException ex)
        {
            System.out.println("Error " + ex);
            data[0] = "Error";
            data[1] = "https://www.gravatar.com/avatar/b00a4353a9ad54c5c914a028280c0f3f?s=32&d=identicon&r=PG&f=1";
            return data;
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show)
    {
        llGroupList.setVisibility(show ? View.GONE : View.VISIBLE);
        llGroupList.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                llGroupList.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }
}
