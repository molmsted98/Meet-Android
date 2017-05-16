package com.tsuruta.meet.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.tsuruta.meet.R;
import com.tsuruta.meet.fragments.EventFragment;
import com.tsuruta.meet.objects.Chat;

import java.util.ArrayList;

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerViewHolder>
{
    private ArrayList<Chat> chats;
    private EventFragment parent;

    public ChatRecyclerAdapter(EventFragment parent, ArrayList<Chat> chats)
    {
        this.chats = chats;
        this.parent = parent;
    }

    public void updateList(ArrayList<Chat> chats)
    {
        this.chats = chats;
        notifyDataSetChanged();
    }

    public void chatClicked(int position)
    {
        //parent.ChatClicked(position);
    }

    @Override
    public int getItemCount()
    {
        if(chats == null)
        {
            return 0;
        }
        else
        {
            return chats.size();
        }
    }

    public EventFragment getParent()
    {
        return parent;
    }

    @Override
    public ChatRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int position)
    {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.card_chat, viewGroup, false);
        return new ChatRecyclerViewHolder(this, itemView);
    }

    @Override
    public void onBindViewHolder(ChatRecyclerViewHolder viewHolder, int position)
    {
        if (chats.get(position) != null)
        {
            String message = chats.get(position).getMessage();
            String sender = chats.get(position).getSenderName();
            viewHolder.setMessage(message);

            //Decide which side the chat should be on.
            String side = "left";
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            String currentUid = mAuth.getCurrentUser().getUid();
            if(chats.get(position).getSenderUid().equals(currentUid))
            {
                side = "right";
            }

            //Move chat to the proper side based on sender
            viewHolder.setSide(side);

            //Only show the profile image and sender image if it's the first message in their chain.
            if(position != 0)
            {
                if(!chats.get(position - 1).getSenderUid().equals(chats.get(position).getSenderUid()))
                {
                    viewHolder.setSender(sender);
                    viewHolder.setImage(side);
                    viewHolder.addMargin();
                }
            }
            //Makes sure that the very first chat will set name and image
            if (chats.get(position).getUid().equals(chats.get(0).getUid()))
            {
                viewHolder.setSender(sender);
                viewHolder.setImage(side);
            }
        }
    }
}
