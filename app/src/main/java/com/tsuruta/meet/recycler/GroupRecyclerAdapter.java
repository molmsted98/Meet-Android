package com.tsuruta.meet.recycler;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tsuruta.meet.R;
import com.tsuruta.meet.fragments.GroupListFragment;
import com.tsuruta.meet.objects.Group;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class GroupRecyclerAdapter extends RecyclerView.Adapter<GroupRecyclerViewHolder>
{
    private ArrayList<Group> groups;
    private ArrayList<ArrayList<String>> urlList;
    private GroupListFragment parent;

    public GroupRecyclerAdapter(GroupListFragment parent, ArrayList<Group> groups, ArrayList<ArrayList<String>> urlList)
    {
        this.groups = groups;
        this.urlList = urlList;
        this.parent = parent;
    }

    public void updateList(ArrayList<Group> groups, ArrayList<ArrayList<String>> urlList)
    {
        this.groups = groups;
        this.urlList = urlList;
        notifyDataSetChanged();
    }

    public void groupClicked(int position)
    {
        if(groups.get(position).getHasJoined())
        {
            parent.groupClicked(position);
        }
    }

    public void joinGroup(int position)
    {
        parent.joinGroup(position);
    }

    public void acceptInvite(int position)
    {
        parent.acceptInvite(position);
    }

    public void denyInvite(int position)
    {
        parent.denyInvite(position);
    }

    @Override
    public int getItemCount()
    {
        if(groups == null)
        {
            return 0;
        }
        else
        {
            return groups.size();
        }
    }

    @Override
    public GroupRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int position)
    {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.card_group, viewGroup, false);
        return new GroupRecyclerViewHolder(this, itemView);
    }

    @Override
    public void onBindViewHolder(GroupRecyclerViewHolder viewHolder, int position)
    {
        if (groups.get(position) != null)
        {
            String name = groups.get(position).getTitle();

            //Find creator's name based on userUid
            String creator = groups.get(position).getCreatorName();

            //Calculate expiration date based on the creation date
            long timestamp = groups.get(position).getTimestamp();
            String expires = getDate(timestamp);

            boolean hasJoined = groups.get(position).getHasJoined();

            viewHolder.setTvGroupTitle(name);
            viewHolder.setTvGroupCreator(creator);
            viewHolder.setTvGroupExpires(expires);
            viewHolder.setJoinVisibility(hasJoined);
            viewHolder.setAvatarUrls(urlList.get(position));
            viewHolder.setRowIndex(position);

            if(groups.get(position).getInvited())
            {
                //TODO: Pass into this method the user's name.
                viewHolder.setInviter("$user_name");
            }
        }
    }

    public GroupListFragment getParent()
    {
        return parent;
    }

    private String getDate(long timestamp)
    {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp);
        return DateFormat.format("MM-dd-yyyy", cal).toString();
    }
}
