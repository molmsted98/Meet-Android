package com.tsuruta.meet.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

import com.tsuruta.meet.R
import com.tsuruta.meet.activities.MainActivity
import com.tsuruta.meet.objects.Event
import com.tsuruta.meet.recycler.EventRecyclerAdapter

import java.util.ArrayList

class FindEventFragment : Fragment()
{
    private var faActivity: FragmentActivity? = null
    internal lateinit var llLayout: LinearLayout
    internal lateinit var parent: MainActivity
    internal var events = ArrayList<Event>()
    internal var events2 = ArrayList<Event>()
    internal lateinit var recyclerView: RecyclerView
    internal lateinit var recyclerView2: RecyclerView
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var layoutManager2: RecyclerView.LayoutManager? = null
    private var adapter: EventRecyclerAdapter? = null
    private var adapter2: EventRecyclerAdapter? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        faActivity = super.getActivity()
        parent = activity as MainActivity
        llLayout = inflater!!.inflate(R.layout.fragment_findevent, container, false) as LinearLayout
        recyclerView = llLayout.findViewById(R.id.eventRecycler) as RecyclerView
        recyclerView2 = llLayout.findViewById(R.id.eventRecycler2) as RecyclerView

        parent.setAddVisibility(false)
        parent.setBottomNavigationViewVisibility(true)
        events.add(Event("Little Joes", "Grand Blanc", "A little get together I guess.", "5/7/17", "7:20 PM", "https://scontent.fdet1-1.fna.fbcdn.net/v/t1.0-0/s600x600/16602596_10158223421880261_6566032323955305214_n.jpg?oh=4f2eab07a1c1128810fe8afa191900be&oe=59A3A1A3"))
        events.add(Event("DnD", "Silas' House", "Nerd stuff, duh.", "6/24/17", "5:30 PM", "https://scontent.fdet1-1.fna.fbcdn.net/v/t1.0-0/s600x600/17884020_1305653222816529_8315348682852686610_n.jpg?oh=24c780402e53908ad3758ed7ae4e3b16&oe=59BB9156"))
        events2.add(Event("Matt's Banger", "Canton", "You have 5 friends attending.", "6/2/17", "4:20 PM", "https://scontent.fdet1-1.fna.fbcdn.net/v/t1.0-0/s600x600/18199159_1384354608325281_4779140821264668469_n.jpg?oh=e95dabcbc313ef17cc32aba23d28c935&oe=59B6F358"))
        events2.add(Event("Chance Concert", "The Palace", "You have 1 friends attending.", "5/24/17", "2:20 PM", "https://scontent.fdet1-1.fna.fbcdn.net/v/t1.0-9/18446754_451940161808386_3251416077937740_n.jpg?oh=5cd352c442dc257640874ffdae01c7d5&oe=59AB1550"))
        setupRecycler()

        return llLayout
    }

    private fun setupRecycler()
    {
        recyclerView.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(faActivity, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager
        adapter = EventRecyclerAdapter(this, events)
        recyclerView.adapter = adapter

        recyclerView2.setHasFixedSize(true)
        layoutManager2 = LinearLayoutManager(faActivity, LinearLayoutManager.HORIZONTAL, false)
        recyclerView2.layoutManager = layoutManager2
        adapter2 = EventRecyclerAdapter(this, events2)
        recyclerView2.adapter = adapter2
    }

    fun eventClicked(position: Int)
    {
        println("Event Clicked at " + position)
    }

    fun getParent(): MainActivity = parent

    companion object
    {
        fun newInstance(): FindEventFragment = FindEventFragment()
    }
}
