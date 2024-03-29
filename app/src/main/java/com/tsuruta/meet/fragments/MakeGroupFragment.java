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
import android.support.v4.app.FragmentManager;
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
import com.tsuruta.meet.objects.Group;

public class MakeGroupFragment extends Fragment implements View.OnClickListener
{
    FragmentActivity faActivity;
    LinearLayout llLayout, llInvites, llCreateGroup;
    MainActivity parent;
    EditText etGroupName;
    Button btnCreateGroup;
    Switch sPublic, sInvites;
    ProgressBar pbCreate;
    private FirebaseAuth mAuth;

    public static MakeGroupFragment newInstance()
    {
        return new MakeGroupFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        faActivity = super.getActivity();
        parent = (MainActivity)getActivity();
        llLayout = (LinearLayout)inflater.inflate(R.layout.fragment_makegroup, container, false);

        mAuth = FirebaseAuth.getInstance();

        etGroupName = (EditText) llLayout.findViewById(R.id.etGroupName);
        btnCreateGroup = (Button) llLayout.findViewById(R.id.btnCreateGroup);
        sPublic = (Switch) llLayout.findViewById(R.id.sPublic);
        sInvites = (Switch) llLayout.findViewById(R.id.sInvites);
        llInvites = (LinearLayout) llLayout.findViewById(R.id.llInvites);
        llCreateGroup = (LinearLayout) llLayout.findViewById(R.id.llCreateGroup);
        pbCreate = (ProgressBar) llLayout.findViewById(R.id.pbCreate);

        btnCreateGroup.setOnClickListener(this);
        sPublic.setOnClickListener(this);
        parent.setAddVisibility(false);
        parent.setBottomNavigationViewVisibility(false);

        return llLayout;
    }

    @Override
    public void onClick(View view)
    {
        if (view == btnCreateGroup)
        {
            showProgress(true);

            final boolean mPublic = sPublic.isChecked();

            final FirebaseUser currentUser = mAuth.getCurrentUser();
            final Group newGroup = new Group(etGroupName.getText().toString(), currentUser.getUid(),
                    sPublic.isChecked(), sInvites.isChecked());

            final String newUid = FirebaseDatabase.getInstance()
                    .getReference()
                    .child(getString(R.string.db_groups))
                    .push().getKey();

            newGroup.setUid(newUid);

            FirebaseDatabase.getInstance()
                    .getReference()
                    .child(getString(R.string.db_groups))
                    .child(newUid)
                    .setValue(newGroup.toMap())
                    .addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                // successfully added group, update member lists
                                FirebaseDatabase.getInstance()
                                        .getReference()
                                        .child(getString(R.string.db_groups))
                                        .child(newUid)
                                        .child(getString(R.string.prop_group_members))
                                        .child(currentUser.getUid())
                                        .setValue(true)
                                        .addOnCompleteListener(new OnCompleteListener<Void>()
                                        {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task)
                                            {
                                                if (task.isSuccessful())
                                                {
                                                    // successfully updated members
                                                    showProgress(false);
                                                    faActivity.getSupportFragmentManager()
                                                            .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                                                    faActivity.getSupportFragmentManager()
                                                            .beginTransaction()
                                                            .add(R.id.content_container, GroupListFragment.newInstance(), getString(R.string.fragment_grouplist_name))
                                                            .addToBackStack(getString(R.string.fragment_grouplist_name))
                                                            .commit();
                                                }
                                                else
                                                {
                                                    //Failed to add owner to member list
                                                    Toast.makeText(faActivity.getApplicationContext(), "Failed to update members", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                            }
                            else
                            {
                                // failed to add group
                                Toast.makeText(faActivity.getApplicationContext(), "Failed to create group", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
        else if(view == sPublic)
        {
            if(sPublic.isChecked())
            {
                llInvites.setVisibility(View.GONE);
                sInvites.setChecked(true);
            }
            else
            {
                llInvites.setVisibility(View.VISIBLE);
                sInvites.setChecked(true);
            }
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show)
    {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        llCreateGroup.setVisibility(show ? View.GONE : View.VISIBLE);
        llCreateGroup.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                llCreateGroup.setVisibility(show ? View.GONE : View.VISIBLE);
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
