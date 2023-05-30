package com.example.myapplication2

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider


import androidx.viewpager2.widget.ViewPager2

class ImgGenFragment : Fragment() {
    private var description: String = ""

    private lateinit var imageView: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var refreshButton: ImageButton

    private lateinit var viewModel: ImgGenViewModel


    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.img_gen_fragment, container, false)
        viewModel = ViewModelProvider(this).get(ImgGenViewModel::class.java)


        super.onViewCreated(view, savedInstanceState)

        viewPager = view.findViewById(R.id.viewPager)

        imageView = view.findViewById(R.id.imageView)
        progressBar = view.findViewById(R.id.progressBar)
        refreshButton = view.findViewById(R.id.refreshButton)

        // 이미지 생성 로직을 실행하는 코드

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                // 현재 페이지의 위치(position)을 확인하여 알맞은 동작을 수행합니다.
                when (position) {
                    1 -> {
                        val StoryFragment = StoryFragment()
                        // Fragment 전환 코드를 작성하세요.
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.viewPager, StoryFragment)
                            .commit()
                        // 포지션 1에 도착하였을 때의 동작을 정의합니다.
                        // 이곳에서 필요한 로직을 추가하세요.
                    }
                }
            }
        })

        refreshButton.setOnClickListener {
            if (description.isEmpty()) {
                Toast.makeText(requireContext(), "데이터가 아직 준비되지 않았습니다.", Toast.LENGTH_SHORT).show()
            } else {
                // updateDescription 메소드 호출
                updateDescription(description)
            }
        }


        viewModel.imageBitmap.observe(viewLifecycleOwner, Observer { bitmap ->
            imageView.setImageBitmap(bitmap)
            imageView.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        })



        return view
    }

    fun updateDescription(newDescription: String) {
        description = newDescription
        // Chaquopy를 백그라운드에서 실행하는 로직을 호출합니다.
        viewModel.generateImage(description, requireContext())
    }



}
