package com.tsuruta.meet.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.tsuruta.meet.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AvatarRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener
{
    private ArrayList<String> urlList = new ArrayList<>();
    private int mRowIndex = -1;
    private GroupRecyclerAdapter era;

    public AvatarRecyclerAdapter(GroupRecyclerAdapter era)
    {
        this.era = era;
    }

    public void setData(ArrayList<String> data)
    {
        if (urlList != data)
        {
            urlList = data;
            notifyDataSetChanged();
        }
    }

    public void setRowIndex(int index)
    {
        mRowIndex = index;
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private CircleImageView civAvatar;

        public ItemViewHolder(View itemView)
        {
            super(itemView);
            civAvatar = (CircleImageView) itemView.findViewById(R.id.civAvatar);
            civAvatar.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            era.groupClicked(mRowIndex);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.card_avatar, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder rawHolder, int position)
    {
        ItemViewHolder holder = (ItemViewHolder) rawHolder;
        Glide
            .with(era.getParent())
            .load(urlList.get(position))
            .centerCrop()
            .crossFade()
            .into(holder.civAvatar);
    }

    @Override
    public void onClick(View view)
    {
        era.groupClicked(mRowIndex);
    }

    @Override
    public int getItemCount()
    {
        return urlList.size();
    }
}
