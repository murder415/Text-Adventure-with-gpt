package com.example.myapplication2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator




class StoryActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var storyAdapter: StoryAdapter
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.story_activity)

        viewPager = findViewById(R.id.viewpager)
        tabLayout = findViewById(R.id.tabLayout)

        storyAdapter = StoryAdapter(supportFragmentManager, lifecycle)
        storyAdapter.addFragment(StoryFragment())
        storyAdapter.addFragment(ImgGenFragment())
        storyAdapter.addFragment(ChoiceFragment())
        viewPager.adapter = storyAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = "Tab ${position + 1}"
        }.attach()

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> {
                        replaceFragment(ImgGenFragment())
                    }
                    1 -> {
                        replaceFragment(StoryFragment())
                    }
                    2 -> {
                        replaceFragment(ChoiceFragment())
                    }
                }
            }
        })
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    class StoryAdapter(
        fragmentManager: FragmentManager,
        lifecycle: Lifecycle
    ) : FragmentStateAdapter(fragmentManager, lifecycle) {
        private val fragmentList: MutableList<Fragment> = ArrayList()

        fun addFragment(fragment: Fragment) {
            fragmentList.add(fragment)
        }

        override fun getItemCount(): Int {
            return fragmentList.size
        }

        override fun createFragment(position: Int): Fragment {
            return fragmentList[position]
        }
    }
}
