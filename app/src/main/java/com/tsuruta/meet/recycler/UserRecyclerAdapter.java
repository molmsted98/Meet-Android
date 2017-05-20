package com.tsuruta.meet.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tsuruta.meet.R;
import com.tsuruta.meet.fragments.InviteFragment;
import com.tsuruta.meet.objects.User;

import java.util.ArrayList;

public class UserRecyclerAdapter extends RecyclerView.Adapter<UserRecyclerViewHolder>
{
    private ArrayList<User> users;
    private InviteFragment parent;

    public UserRecyclerAdapter(InviteFragment parent, ArrayList<User> users)
    {
        this.users = users;
        this.parent = parent;
    }

    public void updateList(ArrayList<User> users)
    {
        this.users = users;
        notifyDataSetChanged();
    }

    public void userClicked(int position, boolean selected)
    {
        parent.userClicked(position, selected);
    }

    @Override
    public int getItemCount()
    {
        if(users == null)
        {
            return 0;
        }
        else
        {
            return users.size();
        }
    }

    public InviteFragment getParent()
    {
        return parent;
    }

    @Override
    public UserRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int position)
    {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.card_user, viewGroup, false);
        return new UserRecyclerViewHolder(this, itemView);
    }

    @Override
    public void onBindViewHolder(UserRecyclerViewHolder viewHolder, int position)
    {
        if (users.get(position) != null)
        {
            viewHolder.setTvName(users.get(position).getEmail());
        }
    }
}
