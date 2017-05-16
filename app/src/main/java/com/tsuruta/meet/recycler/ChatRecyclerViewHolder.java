package com.tsuruta.meet.recycler;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tsuruta.meet.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    ChatRecyclerAdapter parent;
    TextView tvMessage, tvSenderName;
    LinearLayout llChatRow, llNameAndMessage;
    CircleImageView ivLeft, ivRight;

    public ChatRecyclerViewHolder(ChatRecyclerAdapter parent, View itemView)
    {
        super(itemView);
        this.parent = parent;
        tvMessage = (TextView) itemView.findViewById(R.id.tvMessage);
        tvSenderName = (TextView) itemView.findViewById(R.id.tvSenderName);
        llChatRow = (LinearLayout) itemView.findViewById(R.id.llChatRow);
        llNameAndMessage = (LinearLayout) itemView.findViewById(R.id.llNameAndMessage);
        ivLeft = (CircleImageView) itemView.findViewById(R.id.ivProfileLeft);
        ivRight = (CircleImageView) itemView.findViewById(R.id.ivProfileRight);
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

    public void setSender(String senderName)
    {
        tvSenderName.setVisibility(View.VISIBLE);
        tvSenderName.setText(senderName);
    }

    public void setImage(String side, String url)
    {
        ImageView iv = ivLeft;
        if(side.equals("left"))
        {
            ivRight.setVisibility(View.GONE);
            ivLeft.setVisibility(View.VISIBLE);
        }
        else if(side.equals("right"))
        {
            iv = ivRight;
            ivLeft.setVisibility(View.GONE);
            ivRight.setVisibility(View.VISIBLE);
        }

        Glide
                .with(parent.getParent())
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.com_facebook_button_like_background)
                .crossFade()
                .dontAnimate()
                .into(iv);
    }

    public void setMessage(String message)
    {
        tvMessage.setText(message);
    }

    public void setSide(String side)
    {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int chatPadding = dpToPx(16);
        int rowPadding = dpToPx(8);
        tvMessage.setMaxWidth(parent.getParent().getParent().getResources().getDisplayMetrics().widthPixels - dpToPx(64));

        if(side.equals("left"))
        {
            params.gravity = Gravity.LEFT;
            tvMessage.setBackground(ContextCompat.getDrawable(parent.getParent().getParent(), R.drawable.chat_left));
            llChatRow.setGravity(Gravity.LEFT);
            tvMessage.setTextColor(ContextCompat.getColor(parent.getParent().getParent(), R.color.left_text));
            tvMessage.setLayoutParams(params);
            tvSenderName.setLayoutParams(params);
            tvSenderName.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            llChatRow.setPadding(0, 0, rowPadding, 0);
            tvMessage.setPadding(chatPadding,chatPadding,chatPadding,chatPadding);
        }
        else if(side.equals("right"))
        {
            params.gravity = Gravity.RIGHT;
            tvMessage.setBackground(ContextCompat.getDrawable(parent.getParent().getParent(), R.drawable.chat_right));
            llChatRow.setGravity(Gravity.RIGHT);
            tvMessage.setTextColor(ContextCompat.getColor(parent.getParent().getParent(), R.color.right_text));
            tvMessage.setLayoutParams(params);
            tvSenderName.setLayoutParams(params);
            tvSenderName.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            llChatRow.setPadding(rowPadding, 0, 0, 0);
            tvMessage.setPadding(chatPadding,chatPadding,chatPadding,chatPadding);
        }
    }

    public void addMargin()
    {
        int newMargin = dpToPx(16);
        llChatRow.setPadding(0, newMargin, 0, 0);
    }

    private int dpToPx(int dp)
    {
        final float scale = parent.getParent().getParent().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
