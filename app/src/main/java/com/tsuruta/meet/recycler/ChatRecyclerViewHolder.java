package com.tsuruta.meet.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsuruta.meet.R;

public class ChatRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    ChatRecyclerAdapter parent;
    TextView tvMessage;
    LinearLayout llChat, llChatRow;
    Button btnJoin;

    public ChatRecyclerViewHolder(ChatRecyclerAdapter parent, View itemView)
    {
        super(itemView);
        this.parent = parent;
        tvMessage = (TextView) itemView.findViewById(R.id.tvMessage);
        llChat = (LinearLayout) itemView.findViewById(R.id.llChat);
        llChatRow = (LinearLayout) itemView.findViewById(R.id.llChatRow);
        tvMessage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        if (v == tvMessage)
        {
                int position = getLayoutPosition();
                parent.chatClicked(position);
                System.out.println("Chat was clicked");
        }
    }

    public void setMessage(String message)
    {
        tvMessage.setText(message);
    }

    public void setSide(String side)
    {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        if(side.equals("left"))
        {
            params.gravity = Gravity.LEFT;
            llChat.setLayoutParams(params);
            llChatRow.setPadding(0, 300, 0, 0);
        }
        else if(side.equals("right"))
        {
            params.gravity = Gravity.RIGHT;
            llChat.setLayoutParams(params);
            llChatRow.setPadding(300, 0, 0, 0);
        }
    }
}
