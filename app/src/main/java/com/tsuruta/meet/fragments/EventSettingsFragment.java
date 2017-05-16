package com.tsuruta.meet.fragments;

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
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.tsuruta.meet.R;
import com.tsuruta.meet.activities.MainActivity;
import com.tsuruta.meet.objects.Event;

public class EventSettingsFragment extends Fragment implements View.OnClickListener
{
    FragmentActivity faActivity;
    RelativeLayout llLayout;
    LinearLayout llPublic, llInvites;
    MainActivity parent;
    Event event;
    boolean mPublic, invite, owner;
    Button btnSave, btnInvite, btnLeave, btnDelete;
    Switch sPublic, sInvites;
    EditText etEventName;

    public static EventSettingsFragment newInstance(Event event)
    {
        EventSettingsFragment ef = new EventSettingsFragment();
        ef.event = event;
        return ef;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        faActivity = super.getActivity();
        parent = (MainActivity) getActivity();
        llLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_eventsettings, container, false);
        llPublic = (LinearLayout) llLayout.findViewById(R.id.llPublic);
        llInvites = (LinearLayout) llLayout.findViewById(R.id.llInvites);
        btnSave = (Button) llLayout.findViewById(R.id.btnSaveChanges);
        btnInvite = (Button) llLayout.findViewById(R.id.btnSettingsInvite);
        btnLeave = (Button) llLayout.findViewById(R.id.btnLeaveEvent);
        btnDelete = (Button) llLayout.findViewById(R.id.btnDeleteEvent);
        sPublic = (Switch) llLayout.findViewById(R.id.sPublic);
        sInvites = (Switch) llLayout.findViewById(R.id.sInvites);
        etEventName = (EditText) llLayout.findViewById(R.id.etEventName);
        btnSave.setOnClickListener(this);
        btnInvite.setOnClickListener(this);
        btnLeave.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        sPublic.setOnClickListener(this);
        parent.setAddVisibility(false);

        //Determines what access the user should have for event settings
        owner = (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(event.getCreator()));
        mPublic = event.getPublic();
        invite = event.getInvite();

        setupSettings();

        return llLayout;
    }

    @Override
    public void onClick(View view)
    {
        if(view == btnSave)
        {
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child(getString(R.string.db_events))
                    .child(event.getUid())
                    .child("public")
                    .setValue(sPublic.isChecked())
                    .addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                FirebaseDatabase.getInstance()
                                        .getReference()
                                        .child(getString(R.string.db_events))
                                        .child(event.getUid())
                                        .child("invite")
                                        .setValue(sInvites.isChecked())
                                        .addOnCompleteListener(new OnCompleteListener<Void>()
                                        {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task)
                                            {
                                                if (task.isSuccessful())
                                                {
                                                    FirebaseDatabase.getInstance()
                                                            .getReference()
                                                            .child(getString(R.string.db_events))
                                                            .child(event.getUid())
                                                            .child("title")
                                                            .setValue(etEventName.getText().toString())
                                                            .addOnCompleteListener(new OnCompleteListener<Void>()
                                                            {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task)
                                                                {
                                                                    if (task.isSuccessful())
                                                                    {
                                                                        faActivity.getSupportFragmentManager().popBackStack();
                                                                    }
                                                                    else
                                                                    {
                                                                        // failed to delete event
                                                                        Toast.makeText(faActivity.getApplicationContext(), "Failed to update event", Toast.LENGTH_LONG).show();
                                                                    }
                                                                }
                                                            });
                                                }
                                                else
                                                {
                                                    // failed to delete event
                                                    Toast.makeText(faActivity.getApplicationContext(), "Failed to update event", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                            }
                            else
                            {
                                // failed to delete event
                                Toast.makeText(faActivity.getApplicationContext(), "Failed to update event", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
        else if(view == btnInvite)
        {
            faActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content_container, InviteFragment.newInstance(event.getUid()), "invite")
                    .addToBackStack("eventSettings")
                    .commit();
        }
        else if(view == btnLeave)
        {
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child(getString(R.string.db_events))
                    .child(event.getUid())
                    .child(getString(R.string.db_members))
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                faActivity.getSupportFragmentManager()
                                        .beginTransaction()
                                        .add(R.id.content_container, EventListFragment.newInstance(), getString(R.string.fragment_eventlist_name))
                                        .commit();
                            }
                            else
                            {
                                // failed to delete event
                                Toast.makeText(faActivity.getApplicationContext(), "Failed to leave event", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
        else if(view == btnDelete)
        {
            //TODO: Put in a prompt here asking if they're sure.
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child(getString(R.string.db_events))
                    .child(event.getUid())
                    .removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                faActivity.getSupportFragmentManager()
                                        .beginTransaction()
                                        .add(R.id.content_container, EventListFragment.newInstance(), getString(R.string.fragment_eventlist_name))
                                        .commit();
                            }
                            else
                            {
                                // failed to delete event
                                Toast.makeText(faActivity.getApplicationContext(), "Failed to delete event", Toast.LENGTH_LONG).show();
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

    private void setupSettings()
    {
        sPublic.setChecked(mPublic);
        sInvites.setChecked(invite);
        etEventName.setText(event.getTitle());
        if(owner)
        {
            btnLeave.setVisibility(View.GONE);
        }
        else
        {
            llPublic.setVisibility(View.GONE);
            llInvites.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
            if(!invite)
            {
                btnInvite.setVisibility(View.GONE);
            }
        }
    }
}
