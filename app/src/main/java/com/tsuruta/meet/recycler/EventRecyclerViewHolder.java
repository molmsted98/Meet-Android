package com.tsuruta.meet.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsuruta.meet.R;

public class EventRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    EventRecyclerAdapter parent;
    TextView tvEventTitle, tvEventCreator, tvEventExpires, tvEventInviter;
    Button btnJoin, btnAccept, btnDeny;
    LinearLayout llRespondInvite, llInviter;

    public EventRecyclerViewHolder(EventRecyclerAdapter parent, View itemView)
    {
        super(itemView);
        this.parent = parent;
        llRespondInvite = (LinearLayout) itemView.findViewById(R.id.llRespondInvite);
        llInviter = (LinearLayout) itemView.findViewById(R.id.llInviter);
        tvEventTitle = (TextView) itemView.findViewById(R.id.event_title);
        tvEventCreator = (TextView) itemView.findViewById(R.id.event_creator);
        tvEventExpires = (TextView) itemView.findViewById(R.id.event_expiration);
        tvEventInviter = (TextView) itemView.findViewById(R.id.event_inviter);
        btnJoin = (Button) itemView.findViewById(R.id.btnJoin);
        btnAccept = (Button) itemView.findViewById(R.id.btnAccept);
        btnDeny = (Button) itemView.findViewById(R.id.btnDeny);
        itemView.setOnClickListener(this);
        btnJoin.setOnClickListener(this);
        btnAccept.setOnClickListener(this);
        btnDeny.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        int position = getLayoutPosition();
        if (v == itemView)
        {
            parent.eventClicked(position);
        }
        else if(v == btnJoin)
        {
            parent.joinEvent(position);
        }
        else if(v == btnAccept)
        {
            parent.acceptInvite(position);
        }
        else if(v == btnDeny)
        {
            parent.denyInvite(position);
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

    public void setInviter(String name)
    {
        btnJoin.setVisibility(View.GONE);
        llInviter.setVisibility(View.VISIBLE);
        tvEventInviter.setVisibility(View.VISIBLE);
        //TODO: Set the name on the TV to whatever was passed.
        tvEventInviter.setText("INVITED BY $USER");
        llRespondInvite.setVisibility(View.VISIBLE);
    }
}
