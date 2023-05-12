package com.example.myapplication2

import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Im
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
import java.io.LineNumberReader


class MainActivity : AppCompatActivity() {

    companion object {
        const val apiKey = "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"
    }


    private lateinit var imageView: ImageView
    private lateinit var newImageButton: Button
    private lateinit var startOverButton: Button
    private lateinit var titleTextView: TextView

    private val url = "https://api.openai.com/v1/images/generations"
    private val prompt = "#zombie #apocalypse #survival #news #emergency #preparation #danger #panic #escape #weapon #self-defense #teamwork #communication #resources #shelter #food #water #first-aid #risk-assessment #adaptation"
    private val size = "256x256"
    private val numImages = 1
    private val responseFormat = "url"

    private var isStoryGenerated = false


    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)

        if (isStoryGenerated) {
            setContentView(R.layout.main_activity)



            imageView = findViewById(R.id.imageView)
            newImageButton = findViewById(R.id.newImageButton)
            startOverButton = findViewById(R.id.startButton)
            titleTextView = findViewById(R.id.titleTextView)

            newImageButton.setOnClickListener {
                loadImage()
            }

        } else {
            // 이야기 생성이 되어있는 경우, MainActivity1을 보여줍니다.
            setContentView(R.layout.first_loby)

            startOverButton = findViewById(R.id.startButton)
            titleTextView = findViewById(R.id.titleTextView)
        }

        imageView = findViewById(R.id.imageView)



        // 초기 이미지 로드

        //loadImage()


        // 새 이미지 버튼 클릭 시


        // 시작하기 버튼 클릭 시
        startOverButton.setOnClickListener {
            // 여기에 시작하기 버튼 클릭 시의 동작 구현

            findViewById<ImageView>(R.id.imageView).visibility = View.GONE
            findViewById<TextView>(R.id.titleTextView).visibility = View.GONE
            findViewById<Button>(R.id.startButton).visibility = View.GONE



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


        }



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