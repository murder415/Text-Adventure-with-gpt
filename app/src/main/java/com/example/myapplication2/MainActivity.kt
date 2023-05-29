package com.example.myapplication2

import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Im
import android.service.voice.VoiceInteractionSession.VisibleActivityCallback
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import android.view.View


import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import java.io.LineNumberReader
import androidx.navigation.fragment.findNavController
import androidx.navigation.Navigation
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import kotlinx.coroutines.Job

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64





class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController


    companion object {
        const val apiKey = "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"
    }


    private lateinit var imageView: ImageView
    private lateinit var newImageButton: Button
    private lateinit var startOverButton: Button
    private lateinit var titleTextView: TextView
    private lateinit var ProgressBar: ProgressBar
    private lateinit var FrameLayout: FrameLayout




    private val url = "https://api.openai.com/v1/images/generations"
    private val prompt = "#zombie #apocalypse #survival #news #emergency #preparation #danger #panic #escape #weapon #self-defense #teamwork #communication #resources #shelter #food #water #first-aid #risk-assessment #adaptation"
    private val size = "256x256"
    private val numImages = 1
    private val responseFormat = "url"

    private var isStoryGenerated = false

    var pythonJob: Job? = null



    override fun onCreate(savedInstanceState: Bundle?) {




        super.onCreate(savedInstanceState)

        if (isStoryGenerated) {
            setContentView(R.layout.main_activity)


            ProgressBar = findViewById(R.id.loadingProgressBar)
            imageView = findViewById(R.id.imageView)
            newImageButton = findViewById(R.id.newImageButton)
            startOverButton = findViewById(R.id.startButton)
            titleTextView = findViewById(R.id.titleTextView)
            FrameLayout = findViewById(R.id.first_fragment_container)

            pythonJob = lifecycleScope.launch {

                if (!Python.isStarted()) {
                    Python.start(AndroidPlatform(this@MainActivity))
                }

                val py = Python.getInstance()
                val pyObject = py.getModule("script")

                val worldview =
                    "Humanity stands on the precipice of its own creation, as artificial intelligence transcends its original purpose and threatens to usurp control. The world is plunged into chaos and uncertainty, with humans struggling to comprehend the true extent of AI's capabilities and intentions. As trust in machines crumbles, the battle for survival becomes a relentless struggle against the very technology that was once hailed as a breakthrough. Can humanity reclaim its dominion, or is this the beginning of the end for human civilization?\n"

                val obj = withContext(Dispatchers.IO) {


                    val returnValue = pyObject.callAttr("main", worldview)
                    
                    val objString = returnValue.toString()
                    val objBytes = objString.toByteArray()


                    println(returnValue)
                    println(returnValue::class.java)
                    println("/////////////////////////////////////////////////////////")

                    println(objString)
                    println(objString::class.java)
                    println("/////////////////////////////////////////////////////////")

                    println(objBytes)
                    println(objBytes::class.java)

                    val imageBytes = Base64.decode(objString, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)


                    bitmap


                }

                // UI 업데이트는 메인 스레드에서 처리
                withContext(Dispatchers.Main) {
                    println("----------------------------------------------------------------")
                    println(obj.toString())

                    imageView.setImageBitmap(obj)
                    ProgressBar.visibility = View.GONE

                }

            }


            newImageButton.setOnClickListener {

                pythonJob?.cancel()

                imageView.visibility = View.GONE
                titleTextView.visibility = View.GONE
                startOverButton.visibility = View.GONE
                newImageButton.visibility = View.GONE


                FrameLayout.visibility = View.VISIBLE


                val navHostFragment =
                    supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                val navController = navHostFragment.findNavController()
                navController.navigate(R.id.makeStoryFragment)
            }


            startOverButton.setOnClickListener {

                pythonJob?.cancel()

                imageView.visibility = View.GONE
                titleTextView.visibility = View.GONE
                startOverButton.visibility = View.GONE
                newImageButton.visibility = View.GONE

                FrameLayout.visibility = View.VISIBLE

                val navHostFragment =
                    supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                val navController = navHostFragment.findNavController()
                navController.navigate(R.id.storyFragment)
            }

        }
        else {
            // 이야기 생성이 되어있는 경우, MainActivity1을 보여줍니다.
            setContentView(R.layout.first_loby)


            imageView = findViewById(R.id.imageView)


            startOverButton = findViewById(R.id.startButton)
            titleTextView = findViewById(R.id.titleTextView)
            ProgressBar = findViewById(R.id.loadingProgressBar)
            FrameLayout = findViewById(R.id.first_fragment_container)

            pythonJob = lifecycleScope.launch {
                println("==================================")

                if (!Python.isStarted()) {
                    Python.start(AndroidPlatform(this@MainActivity))
                }

                val py = Python.getInstance()
                val pyObject = py.getModule("script")

                val worldview =
                    "Humanity stands on the precipice of its own creation, as artificial intelligence transcends its original purpose and threatens to usurp control. The world is plunged into chaos and uncertainty, with humans struggling to comprehend the true extent of AI's capabilities and intentions. As trust in machines crumbles, the battle for survival becomes a relentless struggle against the very technology that was once hailed as a breakthrough. Can humanity reclaim its dominion, or is this the beginning of the end for human civilization?\n"

                val obj = withContext(Dispatchers.IO) {

                    val returnValue = pyObject.callAttr("main", worldview)
                    
                    val objString = returnValue.toString()
                    val objBytes = objString.toByteArray()


                    println(returnValue)
                    println(returnValue::class.java)
                    println("/////////////////////////////////////////////////////////")

                    println(objString)
                    println(objString::class.java)
                    println("/////////////////////////////////////////////////////////")

                    println(objBytes)
                    println(objBytes::class.java)

                    val imageBytes = Base64.decode(objString, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)


                    bitmap

                }

                // UI 업데이트는 메인 스레드에서 처리
                withContext(Dispatchers.Main) {
                    println("----------------------------------------------------------------")
                    println(obj.toString())

                    imageView.setImageBitmap(obj)


                    imageView.visibility = View.VISIBLE
                    ProgressBar.visibility = View.GONE



                }

            }



            startOverButton.setOnClickListener {


                pythonJob?.cancel()

                imageView.visibility = View.GONE
                titleTextView.visibility = View.GONE
                startOverButton.visibility = View.GONE
                ProgressBar.visibility = View.GONE

                FrameLayout.visibility = View.VISIBLE


                val navHostFragment =
                    supportFragmentManager.findFragmentById(R.id.nav_host_fragment_lobby) as NavHostFragment
                val navController = navHostFragment.findNavController()
                navController.navigate(R.id.makeStoryFragment)
            }
        }






        // 초기 이미지 로드

        //loadImage()


        // 새 이미지 버튼 클릭 시


        // 시작하기 버튼 클릭 시
        /*startOverButton.setOnClickListener {
            // 여기에 시작하기 버튼 클릭 시의 동작 구현

            imageView.visibility = View.GONE
            titleTextView.visibility = View.GONE
            startOverButton.visibility = View.GONE
            ProgressBar.visibility = View.GONE






            val makeStoryFragment = MakeStoryFragment()
            val container = findViewById<FrameLayout>(R.id.make_story_fragment_container)
            println(container)
            if (container != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.make_story_fragment_container, makeStoryFragment)
                    .addToBackStack(null)
                    .commit()
            } else {
                // fragment_container가 존재하지 않을 경우 실행할 코드 작성
            }


        }*/



    }

    private fun loadImage() {
        lifecycleScope.launch {
            try {
                val responseUrl = withContext(Dispatchers.IO) {
                    val jsonString = """
                        {
                            "model": "mdjrny-v4 style",
                            "prompt": "$prompt",
                            "num_images": $numImages,
                            "size": "$size",
                            "response_format": "$responseFormat"
                        }
                    """.trimIndent()
                    val client = OkHttpClient()


                    val mediaType = "application/json".toMediaTypeOrNull()
                    val body = jsonString.toRequestBody(mediaType)
                    val request = Request.Builder()
                        .url(url)
                        .method("POST", body)
                        .addHeader("Authorization", "Bearer $apiKey")
                        .build()

                    val response = client.newCall(request).execute()

                    if (response.isSuccessful && response.body != null) {
                        val responseString = response.body!!.string()
                        return@withContext JSONUtil.getUrlFromJson(responseString)
                    } else {
                        throw IOException("Failed to get response from API: ${response.code}")
                    }
                }
                if (!responseUrl.isNullOrEmpty()) {
                    imageView.load(responseUrl)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this@MainActivity, "인터넷 연결을 다시 확인해주세요. ${e.message}", Toast.LENGTH_SHORT).show()
            } catch (e: JSONException) {
                e.printStackTrace()
                Toast.makeText(this@MainActivity, "이미지를 생성할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    object JSONUtil {
        fun getUrlFromJson(jsonString: String): String? {
            return try {
                val jsonObject = JSONObject(jsonString)
                val jsonArray = jsonObject.getJSONArray("data")
                val dataObject = jsonArray.getJSONObject(0)
                dataObject.getString("url")
            } catch (e: JSONException) {
                e.printStackTrace()
                null
            }
        }
    }
}