package com.tsuruta.meet.recycler;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tsuruta.meet.R;
import com.tsuruta.meet.fragments.EventListFragment;
import com.tsuruta.meet.objects.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EventRecyclerAdapter extends RecyclerView.Adapter<EventRecyclerViewHolder>
{
    private ArrayList<Event> events;
    private ArrayList<ArrayList<String>> urlList;
    private EventListFragment parent;

    public EventRecyclerAdapter(EventListFragment parent, ArrayList<Event> events, ArrayList<ArrayList<String>> urlList)
    {
        this.events = events;
        this.urlList = urlList;
        this.parent = parent;
    }

    public void updateList(ArrayList<Event> events, ArrayList<ArrayList<String>> urlList)
    {
        this.events = events;
        this.urlList = urlList;
        notifyDataSetChanged();
    }

    public void eventClicked(int position)
    {
        if(events.get(position).getHasJoined())
        {
            parent.eventClicked(position);
        }
    }

    public void joinEvent(int position)
    {
        parent.joinEvent(position);
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
        if(events == null)
        {
            return 0;
        }
        else
        {
            return events.size();
        }
    }

    @Override
    public EventRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int position)
    {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.card_event, viewGroup, false);
        return new EventRecyclerViewHolder(this, itemView);
    }

    @Override
    public void onBindViewHolder(EventRecyclerViewHolder viewHolder, int position)
    {
        if (events.get(position) != null)
        {
            String name = events.get(position).getTitle();

            //Find creator's name based on userUid
            String creator = events.get(position).getCreatorName();

            //Calculate expiration date based on the creation date
            long timestamp = events.get(position).getTimestamp();
            String expires = getDate(timestamp);

            boolean hasJoined = events.get(position).getHasJoined();

            viewHolder.setTvEventTitle(name);
            viewHolder.setTvEventCreator(creator);
            viewHolder.setTvEventExpires(expires);
            viewHolder.setJoinVisibility(hasJoined);
            viewHolder.setAvatarUrls(urlList.get(position));
            viewHolder.setRowIndex(position);

            if(events.get(position).getInvited())
            {
                //TODO: Pass into this method the user's name.
                viewHolder.setInviter("$user_name");
            }
        }
    }

    public EventListFragment getParent()
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
