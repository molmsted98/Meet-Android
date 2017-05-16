package com.tsuruta.meet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.tsuruta.meet.fragments.EventListFragment;
import com.tsuruta.meet.fragments.MakeEventFragment;
import com.tsuruta.meet.R;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //Listen to whenever the fragment is changed, so the title/icon is set accordingly
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener()
        {
            public void onBackStackChanged()
            {
                FragmentManager manager = getSupportFragmentManager();

                if (manager != null)
                {
                    if(manager.getBackStackEntryCount() >= 1){
                        String topOnStack = manager.getBackStackEntryAt(manager.getBackStackEntryCount()-1).getName();
                        Log.i("TOP ON BACK STACK",topOnStack);
                        if(topOnStack.equals(getString(R.string.fragment_eventlist_name)))
                        {
                            setActionBarTitle(getString(R.string.app_name));
                        }
                    }

                    /*
                    Fragment someFragment = (Fragment)getSupportFragmentManager().findFragmentById(R.id.fragmentItem);

                    if (someFragment == null) {
                        // this fragment was removed from back stack
                    }*/
                }
            }
        });

        //Look for a new token in case it's a new device
        final String userToken = FirebaseInstanceId.getInstance().getToken();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
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
                            // successfully added tokemn
                            if(savedInstanceState == null)
                            {
                                //TODO: Check to make sure the user is logged in before showing them the event list. Otherwise switch Activities.
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .add(R.id.content_container, EventListFragment.newInstance(), getString(R.string.fragment_eventlist_name))
                                        .addToBackStack(getString(R.string.fragment_eventlist_name))
                                        .commit();
                            }
                        }
                        else
                        {
                            // failed to add token
                            Toast.makeText(getApplicationContext(), "Unable to add token to database", Toast.LENGTH_LONG).show();
                        }
                    }
                });
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
            case R.id.action_settings:
                return true;

            case R.id.action_newEvent:
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.content_container, MakeEventFragment.newInstance(), getString(R.string.fragment_makeevent_name))
                        .addToBackStack(getString(R.string.fragment_makeevent_name))
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
        //Close the app only when back is pressed on the main screen.
        //TODO: This is a pretty trash solution to the problem, fix it please.
        //Problem: You shouldn't be able to switch from MainActivity back to LoginActivity, unless you click LogOut button.
        if(getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName().equals(getString(R.string.fragment_eventlist_name)))
        {
            this.finishAffinity();
        }
        else
        {
            getSupportFragmentManager().popBackStack();
        }
    }

    public void setActionBarTitle(String name)
    {
        Toolbar tb = (Toolbar)findViewById(R.id.my_toolbar);
        TextView tTitle = (TextView)tb.findViewById(R.id.toolbar_title);
        tTitle.setText(name);
    }
}
