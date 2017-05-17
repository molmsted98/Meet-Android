package com.tsuruta.meet.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tsuruta.meet.R;
import com.tsuruta.meet.activities.MainActivity;
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
    private RecyclerView.LayoutManager layoutManager;
    private UserRecyclerAdapter adapter;
    private Handler mHandler;

    public static InviteFragment newInstance(String uid)
    {
        InviteFragment newIf = new InviteFragment();
        newIf.groupUid = uid;
        return newIf;
    }

    public void userClicked(int position, boolean selected, String uid)
    {
        if(selected)
        {
            inviteUsers.add(uid);
        }
        else
        {
            inviteUsers.remove(uid);
        }
        System.out.println("Size of users " + inviteUsers.size());
        if(inviteUsers.size() == 0)
        {
            btnAddUsers.setEnabled(false);
        }
        else
        {
            btnAddUsers.setEnabled(true);
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
        btnAddUsers.setOnClickListener(this);
        mHandler = new Handler(Looper.getMainLooper());

        parent.setAddVisibility(false);

        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://us-central1-meet-c7395.cloudfunctions.net/inviteList").newBuilder();
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
                        .setValue(false);
            }

            faActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content_container, GroupListFragment.newInstance(), "groupList")
                    .commit();
        }
    }

    private void setupRecycler()
    {
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(faActivity);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new UserRecyclerAdapter(this, users);
        recyclerView.setAdapter(adapter);
    }
}
