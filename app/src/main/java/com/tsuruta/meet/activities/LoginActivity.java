package com.tsuruta.meet.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;


import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.tsuruta.meet.R;
import com.tsuruta.meet.objects.User;

import java.util.Iterator;

public class LoginActivity extends AppCompatActivity
{
    // UI references.
    private View mProgressView;
    private View mLoginFormView;
    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mProgressView = findViewById(R.id.login_progress);
        mLoginFormView = findViewById(R.id.llLoginForm);

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                System.out.println("facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel()
            {
                System.out.println("facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error)
            {
                System.out.println("facebook:onError");
                // ...
            }
        });

        //TODO: Make sure Google Play Services is updated (For Push notifications)
        //GoogleApiAvailability.makeGooglePlayServicesAvailable()
    }

    @Override
    protected void onResume() {
        super.onResume();
        //TODO: Make sure Google Play Services is updated (For Push notifications)
        //GoogleApiAvailability.makeGooglePlayServicesAvailable()
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        showProgress(true);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token)
    {
        System.out.println("handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            // Sign in success, update UI with the signed-in user's information
                            System.out.println("signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            addUserToDatabase(getApplicationContext(), user);
                        }
                        else
                        {
                            // If sign in fails, display a message to the user.
                            System.out.println("signInWithCredential:failure" + task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addUserToDatabase(Context context, final FirebaseUser firebaseUser)
    {
        final String userToken = FirebaseInstanceId.getInstance().getToken();
        User user = new User(firebaseUser.getUid(),
                firebaseUser.getEmail(), firebaseUser.getPhotoUrl().toString(), firebaseUser.getDisplayName());
        FirebaseDatabase.getInstance()
                .getReference()
                .child(getString(R.string.db_users))
                .child(firebaseUser.getUid())
                .setValue(user.toMap())
                .addOnCompleteListener(new OnCompleteListener<Void> ()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
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
                                                // successfully added user
                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                                                getApplicationContext().startActivity(intent);
                                                showProgress(false);
                                            }
                                            else
                                            {
                                                // failed to add user
                                                Toast.makeText(getApplicationContext(), "Unable to add user to database", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                            // successfully added user
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                            getApplicationContext().startActivity(intent);
                        }
                        else
                        {
                            // failed to add user
                            Toast.makeText(getApplicationContext(), "Unable to add user to database", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onStart()
    {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        //Already signed in, go to chats
        if(currentUser != null)
        {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);

            //TODO: Reimplement checking DB for user, but not as slow.
            /*
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child(getString(R.string.db_users))
                    .orderByKey()
                    .equalTo(currentUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            if(dataSnapshot.getChildrenCount() != 0)
                            {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getApplicationContext().startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError)
                        {
                            // Unable to retrieve events.
                            Toast.makeText(getApplicationContext(), "Unable to retrieve users", Toast.LENGTH_LONG).show();
                        }
                    });*/
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show)
    {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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

