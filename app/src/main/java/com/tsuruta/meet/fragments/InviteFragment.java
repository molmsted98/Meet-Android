package com.tsuruta.meet.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tsuruta.meet.R;
import com.tsuruta.meet.activities.MainActivity;
import com.tsuruta.meet.objects.Group;
import com.tsuruta.meet.objects.User;
import com.tsuruta.meet.recycler.UserRecyclerAdapter;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
    String groupUid;
    TextView tvNoUsers;
    Group group;
    private RecyclerView.LayoutManager layoutManager;
    private UserRecyclerAdapter adapter;
    private Handler mHandler;
    private View mProgressView;
    private View mInviteListView;
    int shortAnimTime;

    public static InviteFragment newInstance(Group group)
    {
        InviteFragment newIf = new InviteFragment();
        newIf.group = group;
        newIf.groupUid = group.getUid();
        return newIf;
    }

    public void userClicked(int position, boolean selected)
    {
        if(selected)
        {
            inviteUsers.add(users.get(position).getUid());
        }
        else
        {
            inviteUsers.remove(users.get(position).getUid());
        }

        if(inviteUsers.size() == 0)
        {
            btnAddUsers.setEnabled(false);
            btnAddUsers.setAlpha(.5f);
        }
        else
        {
            btnAddUsers.setEnabled(true);
            btnAddUsers.setAlpha(1f);
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
        tvNoUsers = (TextView) llLayout.findViewById(R.id.tvNoUsers);
        btnAddUsers.setOnClickListener(this);
        mHandler = new Handler(Looper.getMainLooper());
        mInviteListView = llLayout.findViewById(R.id.llInviteList);
        mProgressView = llLayout.findViewById(R.id.invite_progress);
        shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        showProgress(true);

        parent.setAddVisibility(false);
        parent.setBottomNavigationViewVisibility(false);

        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(getString(R.string.invite_url)).newBuilder();
        urlBuilder.addQueryParameter("userUid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        urlBuilder.addQueryParameter("groupUid", groupUid);
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException
            {
                if (!response.isSuccessful())
                {
                    throw new IOException("Unexpected code " + response);
                }
                else
                {
                    mHandler.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            try
                            {
                                String jsonResponse = response.body().string();
                                Gson gson = new Gson();
                                users = gson.fromJson(jsonResponse, new TypeToken<ArrayList<User>>(){}.getType());
                                setupRecycler();
                            }
                            catch (IOException ex)
                            {
                                System.out.println("IO Exception " + ex);
                            }
                        }
                    });
                }
            }
        });

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
                        .child(getString(R.string.db_groups))
                        .child(groupUid)
                        .child(getString(R.string.db_members))
                        .child(inviteUsers.get(i))
                        .setValue(false)
                        .addOnCompleteListener(new OnCompleteListener<Void>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if(!task.isSuccessful())
                                {
                                    Toast.makeText(parent.getApplicationContext(), "Failed to invite a user", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
            Toast.makeText(parent.getApplicationContext(), "Users invited", Toast.LENGTH_SHORT).show();
            faActivity.getSupportFragmentManager()
                    .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            faActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content_container, GroupListFragment.newInstance(), getString(R.string.fragment_grouplist_name))
                    .addToBackStack(getString(R.string.fragment_grouplist_name))
                    .commit();

            faActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content_container, GroupFragment.newInstance(group), getString(R.string.fragment_group_name))
                    .addToBackStack(getString(R.string.fragment_group_name))
                    .commit();
        }
    }

    private void setupRecycler()
    {
        if(users.size() == 0)
        {
            recyclerView.setVisibility(View.GONE);
            btnAddUsers.setVisibility(View.GONE);
            tvNoUsers.setVisibility(View.VISIBLE);
        }
        else
        {
            recyclerView.setVisibility(View.VISIBLE);
            btnAddUsers.setVisibility(View.VISIBLE);
            tvNoUsers.setVisibility(View.GONE);
        }
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(faActivity);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new UserRecyclerAdapter(this, users);
        recyclerView.setAdapter(adapter);
        showProgress(false);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show)
    {
        mInviteListView.setVisibility(show ? View.GONE : View.VISIBLE);
        mInviteListView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                mInviteListView.setVisibility(show ? View.GONE : View.VISIBLE);
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
