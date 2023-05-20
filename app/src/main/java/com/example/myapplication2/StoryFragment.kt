package com.example.myapplication2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication2.R

class StoryFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: StoryPagerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.story_fragment, container, false)
        viewPager = view.findViewById(R.id.story_view_pager)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
    }

    private fun setupViewPager() {
        adapter = StoryPagerAdapter()
        viewPager.adapter = adapter
    }

    inner class StoryPagerAdapter : RecyclerView.Adapter<StoryPagerAdapter.StoryViewHolder>() {

        private val messages = mutableListOf<String>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.view_holder_story, parent, false)
            return StoryViewHolder(view)
        }

        override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
            val message = messages[position]
            holder.bind(message)
        }

        override fun getItemCount(): Int {
            return messages.size
        }

        fun addMessage(message: String) {
            messages.add(message)
            notifyDataSetChanged()
        }

        inner class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            private val storyTextView: TextView = itemView.findViewById(R.id.storyTextView)

            fun bind(message: String) {
                storyTextView.text = message
            }
        }
    }
}
