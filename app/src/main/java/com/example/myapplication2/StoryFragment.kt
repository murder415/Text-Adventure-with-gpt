package com.example.myapplication2


import android.app.Dialog
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.lifecycleScope
import android.util.Log

import androidx.viewpager2.widget.ViewPager2
import me.relex.circleindicator.CircleIndicator3






class StoryFragment : Fragment() {

    private lateinit var imgGenFragment: ImgGenFragment
    private lateinit var choiceFragment: ChoiceFragment


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

    var messages: String = ""
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
            showInventoryDialog()
        }

        // Talk 아이콘 클릭 시 ChatbotFragment로 이동
        talkButton.setOnClickListener {

        }

        conditionButton.setOnClickListener {
            showConditionDialog()
        }

        return view
    }


    private fun showInventoryDialog() {
        println("inventory Loop inner")
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.fragment_inventory)

        val closeButton = dialog.findViewById<ImageButton>(R.id.closeButton)
        val inventoryLeftListView = dialog.findViewById<ListView>(R.id.inventoryLeftListView)
        val inventoryRightListView = dialog.findViewById<ListView>(R.id.inventoryRightListView)
        val useButton = dialog.findViewById<Button>(R.id.useButton)
        val discardButton = dialog.findViewById<Button>(R.id.discardButton)

        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        val inventoryList = inventory.toMutableList() // 불변 리스트를 가변 리스트로 변환

        val leftItems = mutableListOf<String>()
        val rightItems = mutableListOf<String>()

        println(inventoryList)

        inventoryList.forEachIndexed { index, item ->
            if ((index + 1) % 2 == 0) {
                rightItems.add(item)
            } else {
                leftItems.add(item)
            }
        }

        println(rightItems)
        println(leftItems)

        val leftAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, leftItems)
        val rightAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, rightItems)

        inventoryLeftListView.adapter = leftAdapter
        inventoryRightListView.adapter = rightAdapter

        leftAdapter.notifyDataSetChanged()
        rightAdapter.notifyDataSetChanged()

        leftItems.forEachIndexed { index, item ->
            Log.d("Inventory", "Left Item ${index + 1}: $item")
        }

        rightItems.forEachIndexed { index, item ->
            Log.d("Inventory", "Right Item ${index + 1}: $item")
        }

        dialog.show()
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
        val weatherTextView2 = dialog.findViewById<TextView>(R.id.weatherTextView2)

        val dayNumberTextView = dialog.findViewById<TextView>(R.id.dayNumberTextView)
        val timePeriodTextView = dialog.findViewById<TextView>(R.id.timePeriodTextView)
        val turnNumberTextView = dialog.findViewById<TextView>(R.id.turnNumberTextView)
        val closeButton = dialog.findViewById<ImageButton>(R.id.closeButton)

        // closeButton 클릭 시 다이얼로그 닫기




        // 다이얼로그에 데이터 설정 및 표시
        difficultyTextView.text = "Difficulty : " + difficulty
        persuasionTextView.text = "리더십   "+abilities["Persuasion"]
        strengthTextView.text = "힘   " + abilities["Strength"]
        intelligenceTextView.text = "지능   " +abilities["Intelligence"]
        dexterityTextView.text = "손재주   "+abilities["Dexterity"]
        luckTextView.text = "행운   "+abilities["Luck"]
        levelTextView.text = level
        xpProgressBar.progress = xp.toInt()
        healthTextView.text = health
        weatherTextView2.text = ""
        weatherTextView.text = weather
        dayNumberTextView.text = currentDay
        if(timePeriod.length > 10){
            timePeriodTextView.textSize = 8.0.toFloat()
        }
        timePeriodTextView.text = timePeriod
        if( weather.length > 10){
            if(weather.length > 20 ){
                weatherTextView2.text = weather
                weatherTextView.text = ""
            }
            weatherTextView.textSize = 7.0.toFloat()
        }

        turnNumberTextView.text = turnNumber

        dialog.setCancelable(false) // 다이얼로그가 취소되지 않도록 설정

        dialog.show()


        closeButton.setOnClickListener {
            dialog.dismiss()
        }

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
                messages = pyObject.callAttr("main", MakeStoryFragment.gameOutput).toString()
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
        gold = data["Gold"] as? String ?: ""

        description= data["Description"] as? String ?: ""

        inventory = data["Inventory"] as? Array<String> ?: emptyArray()
        quest = data["Quest"] as? String ?: ""
        abilities = data["Abilities"] as? Map<String, String> ?: emptyMap()
        commands = data["Possible Commands"] as? Map<String, String> ?: emptyMap()
        val story : String = data["Story"] as? String ?: ""



        val py = Python.getInstance()
        val pyObject = py.getModule("translate")

        val obj = pyObject.callAttr("en2ko", timePeriod)


        val obj2 = pyObject.callAttr("en2ko", location)


        val obj3 = pyObject.callAttr("en2ko", weather)




        val translations = mutableListOf<String>()
        for (item in inventory) {
            val translation = pyObject.callAttr("en2ko", item).toString()

            translations.add(translation)
        }

        val translatedInventory = translations.toTypedArray()


            // 결과를 가지고 추가 작업 수행
            // UI 업데이트는 Dispatchers.Main을 사용하여 메인 스레드에서 수행


                // UI 업데이트 등을 위해 메인 스레드로 전환하여 실행하는 코드 작성
        timePeriod = obj.toString()
        location = obj2.toString()
        weather = obj3.toString()

        inventory = translatedInventory



        // 추출된 데이터 사용
        // ...

        // 예시: 데이터 출력

        imgGenFragment = ImgGenFragment()
        imgGenFragment.updateDescription(description)

        choiceFragment = ChoiceFragment()
        choiceFragment.updatePossibleCommands(commands)



        conditionButton.setOnClickListener {

            showConditionDialog()

        }

        inventoryButton.setOnClickListener {

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

