package com.example.myapplication2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication2.databinding.MakeStoryFragmentBinding
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import coil.load
import kotlinx.coroutines.Dispatchers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONException
import org.json.JSONObject

import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch

import android.text.Editable
import android.text.TextWatcher

import androidx.navigation.fragment.findNavController
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform

import android.widget.ImageView
import android.widget.Button
import android.widget.TextView
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.EditText


data class ImageResponse(val data: List<ImageData>?) // 'data' 타입을 nullable로 변경합니다.data class ImageData(val url: String)
data class ImageData(val url: String)
data class StoryResponse(val choices: List<StoryChoice>)
data class StoryChoice(val text: String)




class MakeStoryFragment : Fragment() {

    private var storySummary: String = ""


    companion object {
        var gameOutput: String = ""
    }

    private lateinit var imageView: ImageView
    private lateinit var progressBar1: ProgressBar
    private lateinit var progressBar2: ProgressBar


    private val client = OkHttpClient()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val storyResponseAdapter = moshi.adapter(StoryResponse::class.java)

    private val oPENAI_API_KEY = MainActivity.apiKey

    private val tAG = "MakeStoryFragment"

    private lateinit var topicEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var summaryText: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.make_story_fragment, container, false)

        topicEditText = rootView.findViewById(R.id.topic_input)
        sendButton = rootView.findViewById(R.id.send_button)
        button1 = rootView.findViewById(R.id.button1)
        button2 = rootView.findViewById(R.id.button2)
        summaryText = rootView.findViewById(R.id.summary_text)
        imageView = rootView.findViewById(R.id.dalle_image)
        progressBar1 = rootView.findViewById(R.id.loadingProgressBar1)
        progressBar2 = rootView.findViewById(R.id.loadingProgressBar2)



        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 사용자가 입력한 주제를 가져오기 위한 EditText
        topicEditText.setShowSoftInputOnFocus(true);

        topicEditText.clearFocus()
        topicEditText.requestFocus()

        // 서버에 요청을 보내고 응답을 처리하기 위한 버튼
        sendButton.setOnClickListener {
            val topic = topicEditText.text.toString()
            topicEditText.visibility = View.GONE
            sendButton.visibility = View.GONE

            progressBar2.visibility = View.VISIBLE
            progressBar2.visibility = View.VISIBLE


            fetchStory(topic)
        }

        button1.setOnClickListener {
            topicEditText.visibility = View.VISIBLE
            sendButton.visibility = View.VISIBLE

            progressBar2.visibility = View.GONE
            progressBar2.visibility = View.GONE
            imageView.visibility = View.GONE
            summaryText.visibility = View.GONE
        }

        button2.setOnClickListener {
            findNavController().navigate(R.id.action_makeStoryFragment_to_storyFragment)
        }

        topicEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 이전 텍스트 변경 이벤트 처리
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 텍스트 변경 이벤트 처리
            }

            override fun afterTextChanged(s: Editable?) {
                // 텍스트 변경 후 이벤트 처리
                if (s?.isNotBlank() == true) {
                    // topic이 입력되어 있는 경우에는 sendButton 표시
                    sendButton.visibility = View.VISIBLE
                } else {
                    // topic이 입력되지 않은 경우에는 sendButton 숨김 처리
                    sendButton.visibility = View.GONE
                }
            }
        })
    }

    private fun fetchStory(topic: String) {
        button1.visibility = View.GONE
        button2.visibility = View.GONE

        progressBar2.visibility = View.VISIBLE
        progressBar1.visibility = View.VISIBLE



        lifecycleScope.launch {

            if (!Python.isStarted()) {
                Python.start(AndroidPlatform(requireContext()))
            }


            val obj = withContext(Dispatchers.IO) {
                val py = Python.getInstance()
                val pyObject = py.getModule("sbj")
                pyObject.callAttr("get_response", topic)
            }

            // UI 업데이트는 메인 스레드에서 처리
            withContext(Dispatchers.Main) {
                gameOutput = obj.toString()

                val summaryIndex = gameOutput.indexOf("Story Summary")
                if (summaryIndex != -1) {
                    val summaryStartIndex = gameOutput.indexOf(':', summaryIndex)
                    if (summaryStartIndex != -1) {
                        val summaryEndIndex = gameOutput.indexOf('\n', summaryStartIndex)
                        if (summaryEndIndex != -1) {
                            storySummary = gameOutput.substring(summaryStartIndex + 1, summaryEndIndex).trim()
                        } else {
                            storySummary = gameOutput.substring(summaryStartIndex + 1).trim()
                        }
                    }
                }

                println(storySummary)


                lifecycleScope.launch {
                    println("4")
                    println(obj.toString())
                    val obj2 = withContext(Dispatchers.IO) {
                        val py = Python.getInstance()
                        val pyObject = py.getModule("translate")
                        pyObject.callAttr("en2ko", obj.toString())
                    }

                    progressBar1.visibility = View.GONE
                    summaryText.visibility = View.VISIBLE
                    summaryText.text = obj2.toString()

                    button1.visibility = View.VISIBLE
                    button2.visibility = View.VISIBLE
                }

            }

        }



        lifecycleScope.launch {

            if (!Python.isStarted()) {
                Python.start(AndroidPlatform(requireContext()))
            }


                val obj = withContext(Dispatchers.IO) {
                val py = Python.getInstance()
                val pyObject = py.getModule("script")
                pyObject.callAttr("main", storySummary)
            }

            // UI 업데이트는 메인 스레드에서 처리
            withContext(Dispatchers.Main) {
                println(obj.toString())
                imageView.load(obj.toString())
                imageView.visibility = View.VISIBLE
                progressBar2.visibility = View.GONE


            }

        }


        /*lifecycleScope.launch {
            try {
                val responseText = withContext(Dispatchers.IO) {
                    val jsonString = """
                    {
                        "prompt": "Please perform the creation theme function of a text adventure game, following the rules listed below:

                                presentation rules
                                
                                1. The game output will always show 'title', 'theme' and 'story summary'
                                
                                2. Wrap all game output in code blocks.
                                
                                3. The subject is \$topic, please write the subject in a little more detail you want so that it can be used as a text adventure game.
                                
                                4. The world view of the topic must be written within 2 lines",
                        "temperature": 0.7,
                        "max_tokens": 1000,
                        "n": 1,
                        "stop": ["\\n\\n"]
                    }
                """.trimIndent()

                    val mediaType = "application/json".toMediaTypeOrNull()
                    val body = jsonString.toRequestBody(mediaType)
                    val storyRequest = Request.Builder()
                        .url("https://api.openai.com/v1/engines/text-davinci-003/completions")
                        .post(body)
                        .addHeader("Authorization", "Bearer $oPENAI_API_KEY")
                        .build()

                    val response = client.newCall(storyRequest).execute()

                    if (response.isSuccessful && response.body != null) {
                        val responseString = response.body!!.string()
                        return@withContext JSONUtil.getTextFromJson(responseString)
                    } else {
                        throw IOException("Failed to get response from API: ${response.code}")
                    }
                }

                if (!responseText.isNullOrEmpty()) {
                    progressBar2.visibility = View.GONE
                    summaryText.visibility = View.VISIBLE
                    summaryText.text = responseText
                    Log.d(TAG, "텍스트 생성 성공: $responseText") // 로그로 출력

                    button1.visibility = View.VISIBLE
                    button2.visibility = View.VISIBLE
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(
                    requireContext(),
                    "인터넷 연결을 다시 확인해주세요!!. ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: JSONException) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "텍스트를 생성할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        */
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

        fun getTextFromJson(jsonString: String): String? {
            return try {
                val jsonObject = JSONObject(jsonString)
                val choicesArray = jsonObject.getJSONArray("choices")
                val choiceObject = choicesArray.getJSONObject(0)
                choiceObject.getString("text")
            } catch (e: JSONException) {
                e.printStackTrace()
                null
            }
        }
    }


}
