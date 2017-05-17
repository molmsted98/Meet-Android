package com.tsuruta.meet.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.tsuruta.meet.fragments.GroupListFragment;
import com.tsuruta.meet.R;
import com.tsuruta.meet.fragments.MakeGroupFragment;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

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
                                                                    loadGroupList(savedInstanceState);
                                                                }
                                                                else
                                                                {
                                                                    //Failed to add name
                                                                    Toast.makeText(getApplicationContext(), "Unable to add name to database", Toast.LENGTH_LONG).show();
                                                                    loadGroupList(savedInstanceState);
                                                                }
                                                            }
                                                        });
                                            }
                                            else
                                            {
                                                //Failed to add photourl
                                                Toast.makeText(getApplicationContext(), "Unable to add photourl to database", Toast.LENGTH_LONG).show();
                                                loadGroupList(savedInstanceState);
                                            }
                                        }
                                    });
                        }
                        else
                        {
                            //Failed to add token
                            Toast.makeText(getApplicationContext(), "Unable to add token to database", Toast.LENGTH_LONG).show();
                            loadGroupList(savedInstanceState);
                        }
                    }
                });
    }

    public void loadGroupList(Bundle savedInstanceState)
    {
        if(savedInstanceState == null)
        {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content_container, GroupListFragment.newInstance(), getString(R.string.fragment_grouplist_name))
                    .addToBackStack(getString(R.string.fragment_grouplist_name))
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
        //Close the app only when back is pressed on the main screen.
        //TODO: This is a pretty trash solution to the problem, fix it please.
        //Problem: You shouldn't be able to switch from MainActivity back to LoginActivity, unless you click LogOut button.
        if(getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName().equals(getString(R.string.fragment_grouplist_name)))
        {
            this.finishAffinity();
        }
        else
        {
            if(getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 2).getName().equals(getString(R.string.fragment_grouplist_name)))
            {
                setAddVisibility(true);
                setActionBarTitle(getString(R.string.app_name));
            }
            getSupportFragmentManager().popBackStack();
        }
    }

    public void setActionBarTitle(String name)
    {
        Toolbar tb = (Toolbar)findViewById(R.id.my_toolbar);
        TextView tTitle = (TextView)tb.findViewById(R.id.toolbar_title);
        tTitle.setText(name);
    }

    public void setAddVisibility(boolean b)
    {
        Toolbar tb = (Toolbar)findViewById(R.id.my_toolbar);
        Menu menu = tb.getMenu();
        MenuItem menuItemAdd = menu.findItem(R.id.action_newGroup);
        menuItemAdd.setVisible(b);
    }
}
