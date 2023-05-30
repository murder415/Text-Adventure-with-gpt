package com.example.myapplication2

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class StoryAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        // 전체 페이지 수를 반환합니다. 여기서는 3으로 설정했습니다.
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        // 현재 페이지의 위치(position)에 따라 알맞은 Fragment를 생성하여 반환합니다.
        when (position) {
            0 -> {
                // 첫 번째 페이지의 Fragment를 생성하여 반환합니다.
                return ImgGenFragment()
            }
            1 -> {
                // 두 번째 페이지의 Fragment를 생성하여 반환합니다.
                return StoryFragment()
            }
            2 -> {
                // 세 번째 페이지의 Fragment를 생성하여 반환합니다.
                return ChoiceFragment()
            }
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}
