package com.tsuruta.meet.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.tsuruta.meet.CenteredToolbar;
import com.tsuruta.meet.fragments.FindEventFragment;
import com.tsuruta.meet.fragments.GroupListFragment;
import com.tsuruta.meet.R;
import com.tsuruta.meet.fragments.MakeGroupFragment;

public class MainActivity extends AppCompatActivity
{
    private BottomNavigationView bottomNavigationView;
    private CenteredToolbar myToolbar;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myToolbar = (CenteredToolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener()
                {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item)
                    {
                        switch (item.getItemId())
                        {
                            //TODO: Add the previous fragment to the backstack
                            case R.id.menu_find_events:
                                getSupportFragmentManager()
                                        .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .add(R.id.content_container, FindEventFragment.Companion.newInstance(), getString(R.string.fragment_findevent_name))
                                        .addToBackStack(getString(R.string.fragment_findevent_name))
                                        .commit();
                                break;
                            case R.id.menu_my_groups:
                                getSupportFragmentManager()
                                        .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .add(R.id.content_container, GroupListFragment.newInstance(), getString(R.string.fragment_grouplist_name))
                                        .addToBackStack(getString(R.string.fragment_grouplist_name))
                                        .commit();
                                break;
                            case R.id.menu_settings:
                                //TODO: Implement settings fragment here
                                break;
                        }
                        return false;
                    }
                });

        //Look for a new token in case it's a new device
        final String userToken = FirebaseInstanceId.getInstance().getToken();
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase.getInstance()
                .getReference()
                .child(getString(R.string.db_users))
                .child(firebaseUser.getUid())
                .child(getString(R.string.db_tokens))
                .child(userToken)
                .setValue(true)
                .addOnCompleteListener(new OnCompleteListener<Void> ()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            //Update photourl
                            final String url = firebaseUser.getPhotoUrl().toString();
                            final String name = firebaseUser.getDisplayName();
                            FirebaseDatabase.getInstance()
                                    .getReference()
                                    .child(getString(R.string.db_users))
                                    .child(firebaseUser.getUid())
                                    .child("avatar")
                                    .setValue(url)
                                    .addOnCompleteListener(new OnCompleteListener<Void> ()
                                    {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                //Update name
                                                FirebaseDatabase.getInstance()
                                                        .getReference()
                                                        .child(getString(R.string.db_users))
                                                        .child(firebaseUser.getUid())
                                                        .child("name")
                                                        .setValue(name)
                                                        .addOnCompleteListener(new OnCompleteListener<Void> ()
                                                        {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task)
                                                            {
                                                                if (task.isSuccessful())
                                                                {
                                                                    //All data updated
                                                                    loadFirstFragment(savedInstanceState);
                                                                }
                                                                else
                                                                {
                                                                    //Failed to add name
                                                                    Toast.makeText(getApplicationContext(), "Unable to add name to database", Toast.LENGTH_LONG).show();
                                                                    loadFirstFragment(savedInstanceState);
                                                                }
                                                            }
                                                        });
                                            }
                                            else
                                            {
                                                //Failed to add photourl
                                                Toast.makeText(getApplicationContext(), "Unable to add photourl to database", Toast.LENGTH_LONG).show();
                                                loadFirstFragment(savedInstanceState);
                                            }
                                        }
                                    });
                        }
                        else
                        {
                            //Failed to add token
                            Toast.makeText(getApplicationContext(), "Unable to add token to database", Toast.LENGTH_LONG).show();
                            loadFirstFragment(savedInstanceState);
                        }
                    }
                });
    }

    public void loadFirstFragment(Bundle savedInstanceState)
    {
        if(savedInstanceState == null)
        {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content_container, FindEventFragment.Companion.newInstance(), getString(R.string.fragment_findevent_name))
                    .addToBackStack(getString(R.string.fragment_findevent_name))
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_newGroup:
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.content_container, MakeGroupFragment.newInstance(), getString(R.string.fragment_makegroup_name))
                        .addToBackStack(getString(R.string.fragment_makegroup_name))
                        .commit();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed()
    {
        String currentFragment = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();

        //Close the app only when back is pressed on the main screen.
        if(currentFragment.equals(getString(R.string.fragment_grouplist_name)))
        {
            this.finishAffinity();
        }
        else if(currentFragment.equals(getString(R.string.fragment_findevent_name)))
        {
            this.finishAffinity();
        }
        else
        {
            String nextFragment = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 2).getName();

            if(nextFragment.equals(getString(R.string.fragment_grouplist_name)))
            {
                setAddVisibility(true);
                setActionBarTitle(getString(R.string.app_name));
                setBottomNavigationViewVisibility(true);
            }
            else if(nextFragment.equals(getString(R.string.fragment_group_name)))
            {
                setAddVisibility(false);
                setBottomNavigationViewVisibility(false);
            }
            else if(nextFragment.equals(getString(R.string.fragment_findevent_name)))
            {
                setAddVisibility(false);
                setBottomNavigationViewVisibility(true);
                setActionBarTitle(getString(R.string.app_name));
            }
            else if(nextFragment.equals(getString(R.string.fragment_groupsettings_name)))
            {
                setAddVisibility(false);
                setBottomNavigationViewVisibility(false);
            }
            getSupportFragmentManager().popBackStack();
        }
    }

    public void setActionBarTitle(String name)
    {
        myToolbar.setTitle(name);
    }

    public void setBottomNavigationViewVisibility(boolean b)
    {
        if(b)
        {
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
        else
        {
            bottomNavigationView.setVisibility(View.GONE);
        }
    }

    public void setAddVisibility(boolean b)
    {
        Menu menu = myToolbar.getMenu();
        MenuItem menuItemAdd = menu.findItem(R.id.action_newGroup);
        menuItemAdd.setVisible(b);
    }
}
