package com.tsuruta.meet.recycler;

import android.support.v4.content.ContextCompat;
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
        int chatPadding = dpToPx(24);
        int rowPadding = dpToPx(40);

        if(side.equals("left"))
        {
            params.gravity = Gravity.LEFT;
            llChat.setBackground(ContextCompat.getDrawable(parent.getParent().getParent(), R.drawable.chat_left));
            llChat.setLayoutParams(params);
            tvMessage.setTextColor(ContextCompat.getColor(parent.getParent().getParent(), R.color.left_text));
            llChatRow.setPadding(0, 0, rowPadding, 0);
            llChat.setPadding(chatPadding,chatPadding,chatPadding,chatPadding);
        }
        else if(side.equals("right"))
        {
            params.gravity = Gravity.RIGHT;
            llChat.setBackground(ContextCompat.getDrawable(parent.getParent().getParent(), R.drawable.chat_right));
            llChat.setLayoutParams(params);
            tvMessage.setTextColor(ContextCompat.getColor(parent.getParent().getParent(), R.color.right_text));
            llChatRow.setPadding(rowPadding, 0, 0, 0);
            llChat.setPadding(chatPadding,chatPadding,chatPadding,chatPadding);
        }
    }

    private int dpToPx(int dp)
    {
        final float scale = parent.getParent().getParent().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
