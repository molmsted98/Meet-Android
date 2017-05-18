package com.tsuruta.meet.recycler

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView

import com.tsuruta.meet.R

class EventRecyclerViewHolder(private var parent: EventRecyclerAdapter, itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener
{
    internal var tvTitle = itemView.findViewById(R.id.tvEventTitle) as TextView
    internal var tvLocation = itemView.findViewById(R.id.tvEventLocation) as TextView
    internal var tvDate = itemView.findViewById(R.id.tvDate) as TextView
    internal var tvTime = itemView.findViewById(R.id.tvTime) as TextView
    internal var rlEventCard = itemView.findViewById(R.id.rlEventCard) as RelativeLayout

    init
    {
        itemView.setOnClickListener(this)
    }

    override fun onClick(v: View)
    {
        val position = layoutPosition
        if (v === itemView)
        {
            parent.eventClicked(position)
        }
    }
}
