package com.tsuruta.meet.recycler

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide

import com.tsuruta.meet.R
import com.tsuruta.meet.fragments.FindEventFragment
import com.tsuruta.meet.objects.Event

import java.util.ArrayList
import android.os.Build
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.Bitmap
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget



class EventRecyclerAdapter(val parent: FindEventFragment, private var events: ArrayList<Event>) : RecyclerView.Adapter<EventRecyclerViewHolder>()
{
    fun updateList(events: ArrayList<Event>)
    {
        this.events = events
        notifyDataSetChanged()
    }

    fun eventClicked(position: Int)
    {
        parent.eventClicked(position)
    }

    override fun getItemCount(): Int = events.size

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): EventRecyclerViewHolder
    {
        val inflater = LayoutInflater.from(viewGroup.context)
        val itemView = inflater.inflate(R.layout.card_event, viewGroup, false)
        return EventRecyclerViewHolder(this, itemView)
    }

    override fun onBindViewHolder(viewHolder: EventRecyclerViewHolder, position: Int)
    {
        viewHolder.tvTitle.text = events[position].name
        viewHolder.tvLocation.text = events[position].location
        viewHolder.tvTime.text = events[position].time
        viewHolder.tvDate.text = events[position].date
        Glide
                .with(parent.getParent())
                .load(events[position].imageUrl)
                .asBitmap()
                .centerCrop()
                .placeholder(R.drawable.com_facebook_button_like_background)
                .into(object : SimpleTarget<Bitmap>(200, 200)
        {
            override fun onResourceReady(resource: Bitmap, glideAnimation: GlideAnimation<in Bitmap>)
            {
                val drawable = BitmapDrawable(parent.resources, resource)
                viewHolder.rlEventCard.background = drawable
            }
        })
    }
}
