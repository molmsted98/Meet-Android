package com.tsuruta.meet.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tsuruta.meet.R;
import com.tsuruta.meet.recycler.EventRecyclerAdapter;

/**
 * Created by michael on 5/1/17.
 */

public class EventRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    EventRecyclerAdapter parent;
    TextView tvEventTitle, tvEventCreator, tvEventExpires;
    Button btnJoin;

    public EventRecyclerViewHolder(EventRecyclerAdapter parent, View itemView)
    {
        super(itemView);
        this.parent = parent;
        tvEventTitle = (TextView) itemView.findViewById(R.id.event_title);
        tvEventCreator = (TextView) itemView.findViewById(R.id.event_creator);
        tvEventExpires = (TextView) itemView.findViewById(R.id.event_expiration);
        btnJoin = (Button) itemView.findViewById(R.id.btnJoin);
        itemView.setOnClickListener(this);
        btnJoin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        if (v == itemView)
        {
            int position = getLayoutPosition();
            parent.eventClicked(position);
        }
        else if(v == btnJoin)
        {
            System.out.println("Tried to join an event");
        }
    }

    public void setTvEventTitle(String title)
    {
        tvEventTitle.setText(title);
    }

    public void setTvEventCreator(String title)
    {
        tvEventCreator.setText("Created by " + title);
    }

    public void setTvEventExpires(String title)
    {
        tvEventExpires.setText("Expires in " + title);
    }

    public void setJoinVisibility(boolean hasJoined)
    {
        if(!hasJoined)
        {
            btnJoin.setVisibility(View.VISIBLE);
        }
    }
}
