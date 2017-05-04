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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.tsuruta.meet.R;
import com.tsuruta.meet.activities.MainActivity;
import com.tsuruta.meet.objects.Event;

import java.util.Random;

public class MakeEventFragment extends Fragment implements View.OnClickListener
{
    FragmentActivity faActivity;
    LinearLayout llLayout, llInvites, llCreateEvent;
    MainActivity parent;
    EditText etEventName;
    Button btnCreateEvent;
    Switch sPublic, sInvites;
    ProgressBar pbCreate;
    private FirebaseAuth mAuth;

    public static MakeEventFragment newInstance()
    {
        return new MakeEventFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        faActivity = super.getActivity();
        parent = (MainActivity)getActivity();
        llLayout = (LinearLayout)inflater.inflate(R.layout.fragment_makeevent, container, false);

        mAuth = FirebaseAuth.getInstance();

        etEventName = (EditText) llLayout.findViewById(R.id.etEventName);
        btnCreateEvent = (Button) llLayout.findViewById(R.id.btnCreateEvent);
        sPublic = (Switch) llLayout.findViewById(R.id.sPublic);
        sInvites = (Switch) llLayout.findViewById(R.id.sInvites);
        llInvites = (LinearLayout) llLayout.findViewById(R.id.llInvites);
        llCreateEvent = (LinearLayout) llLayout.findViewById(R.id.llCreateEvent);
        pbCreate = (ProgressBar) llLayout.findViewById(R.id.pbCreate);

        btnCreateEvent.setOnClickListener(this);
        sPublic.setOnClickListener(this);

        return llLayout;
    }

    @Override
    public void onClick(View view)
    {
        if (view == btnCreateEvent)
        {
            long timestamp = System.currentTimeMillis();
            //Generate a unique ID for the event
            Random rand = new Random();

            int n = rand.nextInt(10000) + 1;
            int n2 = rand.nextInt(10000) + 1;
            final String UID = String.valueOf(timestamp + n + n2);

            showProgress(true);
            final FirebaseUser currentUser = mAuth.getCurrentUser();
            Event newEvent = new Event(etEventName.getText().toString(), currentUser.getUid(),
                    timestamp, sPublic.isChecked(), sInvites.isChecked(), UID);

            FirebaseDatabase.getInstance()
                    .getReference()
                    .child(getString(R.string.db_events))
                    .child(UID)
                    .setValue(newEvent.toMap())
                    .addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                // successfully added event, update member lists
                                updateMembers(currentUser, UID);
                            }
                            else
                            {
                                // failed to add event
                                Toast.makeText(faActivity.getApplicationContext(), "Failed to create event", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
        else if(view == sPublic)
        {
            if(sPublic.isChecked())
            {
                llInvites.setVisibility(View.VISIBLE);
                sInvites.setChecked(true);
            }
            else
            {
                llInvites.setVisibility(View.GONE);
                sInvites.setChecked(false);
            }
        }
    }

    public void updateMembers(final FirebaseUser currentUser, final String eventUid)
    {
        //Add the user to members table
        FirebaseDatabase.getInstance()
                .getReference()
                .child(getString(R.string.db_members) + "/" + eventUid + "/" + currentUser.getUid())
                .setValue(true)
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            //Add this event to the user object
                            FirebaseDatabase
                                    .getInstance()
                                    .getReference()
                                    .child("/" + getString(R.string.db_users) + "/" + currentUser.getUid() + "/" + getString(R.string.db_events) + "/" + eventUid)
                                    .setValue(true)
                                    .addOnCompleteListener(new OnCompleteListener<Void>()
                                    {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                showProgress(false);
                                                faActivity.getSupportFragmentManager()
                                                        .beginTransaction()
                                                        .add(R.id.content_container, EventListFragment.newInstance(), "eventList")
                                                        .commit();
                                            }
                                            else
                                            {
                                                // failed to add event
                                                Toast.makeText(faActivity.getApplicationContext(), "Failed to add event to user", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }
                        else
                        {
                            // failed to add event
                            Toast.makeText(faActivity.getApplicationContext(), "Failed to add user to event", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show)
    {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        llCreateEvent.setVisibility(show ? View.GONE : View.VISIBLE);
        llCreateEvent.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                llCreateEvent.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        pbCreate.setVisibility(show ? View.VISIBLE : View.GONE);
        pbCreate.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                pbCreate.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }
}
