package com.tsuruta.meet.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.tsuruta.meet.fragments.EventListFragment;
import com.tsuruta.meet.fragments.MakeEventFragment;
import com.tsuruta.meet.R;

/**
 * Created by michael on 5/1/17.
 */

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        if(savedInstanceState == null)
        {
            //TODO: Check to make sure the user is logged in before showing them the event list. Otherwise switch Activities.
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content_container, EventListFragment.newInstance(), "eventList")
                    .addToBackStack("eventList")
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;

            case R.id.action_newEvent:
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.content_container, MakeEventFragment.newInstance(), "makeEvent")
                        .addToBackStack("makeEvent")
                        .commit();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onBackPressed() {
        //Close the app only when back is pressed on the main screen.
        //TODO: This is a pretty trash solution to the problem, fix it please.
        //Problem: You shouldn't be able to switch from MainActivity back to LoginActivity, unless you click LogOut button.
        if(getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount()).getName().equals("eventList"))
        {
            this.finishAffinity();
        }
        else
        {
            getSupportFragmentManager().popBackStack();
        }
    }
}
