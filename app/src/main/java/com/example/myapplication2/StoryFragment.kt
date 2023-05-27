package com.example.myapplication2

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.os.Looper

import android.text.SpannableStringBuilder

import android.view.MotionEvent



import android.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import kotlin.reflect.jvm.internal.impl.types.AbstractTypeCheckerContext.SupertypesPolicy.None

import android.app.Dialog
import android.view.Window



class StoryFragment : Fragment() {

    private lateinit var turnNumberTextView: TextView
    private lateinit var timePeriodTextView: TextView
    private lateinit var dayNumberTextView: TextView
    private lateinit var weatherTextView: TextView
    private lateinit var luckTextView : TextView
    private lateinit var healthTextView: TextView
    private lateinit var levelTextView: TextView
    private lateinit var xpProgressBar: ProgressBar
    private lateinit var difficultyTextView : TextView
    private lateinit var persuasionTextView : TextView
    private lateinit var strengthTextView : TextView
    private lateinit var intelligenceTextView : TextView
    private lateinit var dexterityTextView : TextView


    private lateinit var descriptionTextView: TextView
    private lateinit var storyTextView: TextView
    private lateinit var inventoryButton: ImageButton
    private lateinit var talkButton: ImageButton
    private lateinit var progressBar: ProgressBar
    private lateinit var conditionButton : ImageButton
    private lateinit var closeButton : ImageButton


    private var difficulty: String = ""
    private var abilities: Map<String, String> = emptyMap()
    private var level: String = ""
    private var xp: String = "0"
    private var health: String = ""
    private var weather: String = ""
    private var currentDay: String = ""
    private var timePeriod: String = ""
    private var turnNumber: String = ""
    private var ac: String = ""

    private var location: String = ""
    private var description: String = ""
    private var gold: String = ""
    private var inventory: Array<String> = arrayOf()
    private var quest: String = ""
    private var commands: Map<String, String> = emptyMap()



    private var job: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.story_fragment, container, false)


        descriptionTextView = view.findViewById(R.id.descriptionTextView)
        inventoryButton = view.findViewById(R.id.inventoryButton)
        talkButton = view.findViewById(R.id.talkButton)
        progressBar = view.findViewById(R.id.progressBar)
        conditionButton =  view.findViewById(R.id.conditionButton)
        storyTextView = view.findViewById(R.id.storyTextView)


        // Inventory 아이콘 클릭 시 InventoryFragment로 이동
        inventoryButton.setOnClickListener {
            findNavController().navigate(R.id.action_storyFragment_to_inventoryFragment)
        }

        // Talk 아이콘 클릭 시 ChatbotFragment로 이동
        talkButton.setOnClickListener {
            findNavController().navigate(R.id.action_storyFragment_to_chatBotFragment)
        }

        conditionButton.setOnClickListener {
            showConditionDialog()
        }

        return view
    }

    private fun showConditionDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // 다이얼로그 제목 제거
        dialog.setContentView(R.layout.condition) // 커스텀 다이얼로그 레이아웃 설정

        // 커스텀 다이얼로그 내부의 뷰들을 찾아서 초기화
        val difficultyTextView = dialog.findViewById<TextView>(R.id.difficultyTextView)
        val persuasionTextView = dialog.findViewById<TextView>(R.id.persuasionTextView)
        val strengthTextView = dialog.findViewById<TextView>(R.id.strengthTextView)
        val intelligenceTextView = dialog.findViewById<TextView>(R.id.intelligenceTextView)
        val dexterityTextView = dialog.findViewById<TextView>(R.id.dexterityTextView)
        val luckTextView = dialog.findViewById<TextView>(R.id.luckTextView)
        val levelTextView = dialog.findViewById<TextView>(R.id.levelTextView)
        val xpProgressBar = dialog.findViewById<ProgressBar>(R.id.xpProgressBar)
        val healthTextView = dialog.findViewById<TextView>(R.id.healthTextView)
        val weatherTextView = dialog.findViewById<TextView>(R.id.weatherTextView)
        val dayNumberTextView = dialog.findViewById<TextView>(R.id.dayNumberTextView)
        val timePeriodTextView = dialog.findViewById<TextView>(R.id.timePeriodTextView)
        val turnNumberTextView = dialog.findViewById<TextView>(R.id.turnNumberTextView)
        val closeButton = dialog.findViewById<ImageButton>(R.id.closeButton)

        // closeButton 클릭 시 다이얼로그 닫기
        closeButton.setOnClickListener {
            dialog.dismiss()
        }



        // 다이얼로그에 데이터 설정 및 표시
        difficultyTextView.text = difficulty
        persuasionTextView.text = abilities["Persuasion"]
        strengthTextView.text = abilities["Strength"]
        intelligenceTextView.text = abilities["Intelligence"]
        dexterityTextView.text = abilities["Dexterity"]
        luckTextView.text = abilities["Luck"]
        levelTextView.text = level
        xpProgressBar.progress = xp.toInt()
        healthTextView.text = health
        weatherTextView.text = weather
        dayNumberTextView.text = currentDay
        timePeriodTextView.text = timePeriod
        turnNumberTextView.text = turnNumber

        dialog.setCancelable(false) // 다이얼로그가 취소되지 않도록 설정

        dialog.show()


// 다이얼로그 레이아웃의 최상위 뷰에 터치 이벤트 처리기 설정
        val dialogView = dialog.window?.decorView?.findViewById<View>(android.R.id.content)
        dialogView?.setOnTouchListener { view, event ->
            val x = event.rawX.toInt()
            val y = event.rawY.toInt()

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 터치가 시작되면 현재 위치를 저장합니다.
                    dialogView.tag = Pair(x, y)
                }
                MotionEvent.ACTION_MOVE -> {
                    // 이동한 거리를 계산하여 다이얼로그 창의 위치를 업데이트합니다.
                    val (prevX, prevY) = dialogView.tag as? Pair<Int, Int> ?: return@setOnTouchListener false
                    val deltaX = x - prevX
                    val deltaY = y - prevY

                    dialog.window?.let {
                        val params = it.attributes
                        params.x += deltaX
                        params.y += deltaY
                        it.attributes = params
                    }

                    dialogView.tag = Pair(x, y) // 현재 위치를 업데이트합니다.
                }
                MotionEvent.ACTION_UP -> {
                    // 클릭 이벤트를 처리합니다.
                    view.performClick()
                }
            }

            true
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startGame()
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel() // Coroutine 종료
    }

    private fun startGame() {
        if (job?.isActive == true) return

        job = CoroutineScope(Dispatchers.Main).launch {
            progressBar.visibility = View.VISIBLE

            // MakeStoryFragment에서 전달한 주제를 storySummary로 전달
            if (!Python.isStarted()) {
                Python.start(AndroidPlatform(requireContext()))
            }


            val obj = withContext(Dispatchers.IO) {
                val py = Python.getInstance()
                val pyObject = py.getModule("textAdventure")
                pyObject.callAttr("main", MakeStoryFragment.gameOutput)
                val attributes = pyObject.callAttr("return_attributes").toString()
                val answer = pyObject.callAttr("return_answer").toString()
                Pair(attributes, answer)
            }

            val attributes = obj.first

            println("attributes\n")
            handleResult(attributes)
            val answer = obj.second
            println("answer\n")
            println(answer)

            progressBar.visibility = View.GONE
        }
    }

    fun handleResult(result: String) {

        // JSON 문자열을 Map으로 변환
        val data: Map<String, Any> = parseJson(result)

        // 필요한 데이터 추출
        turnNumber = data["Turn number"] as? String ?: ""
        difficulty = data["Difficulty"] as? String ?: ""
        timePeriod = data["Time period of the day"] as? String ?: ""
        currentDay = data["Current day number"] as? String ?: ""
        weather = data["Weather"] as? String ?: ""
        health = data["Health"] as? String ?: ""
        xp = data["XP"] as? String ?: ""
        ac = data["AC"] as? String ?: ""
        level = data["Level"] as? String ?: ""
        location = data["Location"] as? String ?: ""
        description= data["Description"] as? String ?: ""
        gold = data["Gold"] as? String ?: ""
        inventory = data["Inventory"] as? Array<String> ?: emptyArray()
        quest = data["Quest"] as? String ?: ""
        abilities = data["Abilities"] as? Map<String, String> ?: emptyMap()
        commands = data["Possible Commands"] as? Map<String, String> ?: emptyMap()
        val story : String = data["Story"] as? String ?: ""

        // 추출된 데이터 사용
        // ...

        // 예시: 데이터 출력


        conditionButton.setOnClickListener {

            showConditionDialog()

        }

        val typeText2 = description
        println("typeText2")

        println(typeText2)
        animateTextD(typeText2)

        val typedText = story
        println("typeText")

        if ("None".equals(typedText)) {
            // typedText가 "None"인 경우에 실행할 코드
            println("typedText is None");
        } else {
            // typedText가 "None"이 아닌 경우에 실행할 코드
            println("typedText is not None")
            animateText(typedText)
        }
        println(typedText)


    }

    fun parseJson(jsonString: String): Map<String, Any> {
        val gson = Gson()
        val type = object : TypeToken<Map<String, Any>>() {}.type
        return gson.fromJson(jsonString, type)
    }





    private fun animateText(script: String) {
        println("--------------")

        var text = script
        println(script)

        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(requireContext()))
        }


        println("4")

        val py = Python.getInstance()
        val pyObject = py.getModule("translate")
        val obj = pyObject.callAttr("en2ko", script)


            text = obj.toString()

        println(text)
        val typingDelay: Long = 50
        val mediaPlayer = MediaPlayer.create(requireContext(), R.raw.typing_sound)

        var currentIndex = 0
        var isTyping = false

        val handler = Handler(Looper.getMainLooper())
        val spannableBuilder = SpannableStringBuilder()

        fun startTyping() {
            isTyping = true // 초기화 위치 변경

            if (currentIndex < text.length) {
                spannableBuilder.append(text[currentIndex].toString())
                storyTextView.text = spannableBuilder
                currentIndex++
                mediaPlayer.start() // 타이핑 사운드 재생

                handler.postDelayed({
                    startTyping()
                }, typingDelay)
            } else {
                isTyping = false
                mediaPlayer.pause() // 타이핑 사운드 일시 정지
            }
        }

        startTyping()
    }



    private fun animateTextD(script: String) {
        println("--------------")

        println(script)

        var text = script

        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(requireContext()))
        }


        println("1")

        val py = Python.getInstance()
        val pyObject = py.getModule("translate")
        val obj = pyObject.callAttr("en2ko", script)


        text = obj.toString()

        println(text)

        val typingDelay: Long = 50
        val mediaPlayer = MediaPlayer.create(requireContext(), R.raw.typing_sound)

        var currentIndex = 0
        var isTyping = false

        val handler = Handler(Looper.getMainLooper())
        val spannableBuilder = SpannableStringBuilder()

        fun startTyping() {
            isTyping = true // 초기화 위치 변경

            if (currentIndex < text.length) {
                spannableBuilder.append(text[currentIndex].toString())
                descriptionTextView.text = spannableBuilder
                currentIndex++
                mediaPlayer.start() // 타이핑 사운드 재생

                handler.postDelayed({
                    startTyping()
                }, typingDelay)
            } else {
                isTyping = false
                mediaPlayer.pause() // 타이핑 사운드 일시 정지
            }
        }

        startTyping()
    }




    companion object {
        fun newInstance(): StoryFragment {
            return StoryFragment()
        }
    }
}


