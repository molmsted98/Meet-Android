package com.tsuruta.meet.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.tsuruta.meet.R;
import com.tsuruta.meet.fragments.GroupFragment;
import com.tsuruta.meet.objects.Chat;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerViewHolder>
{
    private ArrayList<Chat> chats;
    private GroupFragment parent;

    public ChatRecyclerAdapter(GroupFragment parent, ArrayList<Chat> chats)
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

    public GroupFragment getParent()
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
            //Set the message for the chat
            String message = chats.get(position).getMessage();
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
                    String[] userData = getSavedUserData(chats.get(position).getSenderUid());
                    viewHolder.setSender(userData[0]);
                    viewHolder.setImage(side, userData[1]);
                    viewHolder.addMargin();
                }
            }
            //Makes sure that the very first chat will set name and image
            if (chats.get(position).getUid().equals(chats.get(0).getUid()))
            {
                String[] userData = getSavedUserData(chats.get(position).getSenderUid());
                viewHolder.setSender(userData[0]);
                viewHolder.setImage(side, userData[1]);
            }
        }
    }

    private String[] getSavedUserData(String uid)
    {
        String[] data = new String[2];
        try
        {
            FileInputStream fis = parent.getParent().getApplicationContext().openFileInput(parent.getString(R.string.USERS_FILENAME));
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if(line.equals(uid))
                {
                    data[0] = bufferedReader.readLine();
                    data[1] = bufferedReader.readLine();
                    break;
                }
            }
            return data;
        }
        catch (IOException ex)
        {
            System.out.println("Error " + ex);
            data[0] = "Error";
            data[1] = "https://www.gravatar.com/avatar/b00a4353a9ad54c5c914a028280c0f3f?s=32&d=identicon&r=PG&f=1";
            return data;
        }
    }
}
