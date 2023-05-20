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




data class ImageResponse(val data: List<ImageData>?) // 'data' 타입을 nullable로 변경합니다.data class ImageData(val url: String)
data class ImageData(val url: String)
data class StoryResponse(val choices: List<StoryChoice>)
data class StoryChoice(val text: String)




class MakeStoryFragment : Fragment() {

    companion object {
        private const val TAG = "MakeStoryFragment"
    }

    private val client = OkHttpClient()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val storyResponseAdapter = moshi.adapter(StoryResponse::class.java)

    private val oPENAI_API_KEY = MainActivity.apiKey

    private val tAG = "MakeStoryFragment"


    private lateinit var binding: MakeStoryFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MakeStoryFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 사용자가 입력한 주제를 가져오기 위한 EditText
        binding.topicInput.clearFocus()
        binding.topicInput.requestFocus()
        val topicEditText = binding.topicInput

        // 서버에 요청을 보내고 응답을 처리하기 위한 버튼
        val sendButton = binding.sendButton
        sendButton.setOnClickListener {
            val topic = topicEditText.text.toString()
            fetchStory(topic)


        }

        binding.button1.setOnClickListener {
            val topic = topicEditText.text.toString()
            fetchStory(topic)
        }

        binding.button2.setOnClickListener {
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
        lifecycleScope.launch {
            try {
                val responseUrl = withContext(Dispatchers.IO) {

                    val jsonString = """
                        {
                            "model": "mdjrny-v4 style",
                            "prompt": "$topic",
                            "num_images": 1,
                            "size": "512x512",
                            "response_format": "url"
                        }
                    """.trimIndent()

                    val client = OkHttpClient()


                    val mediaType = "application/json".toMediaTypeOrNull()
                    val body = jsonString.toRequestBody(mediaType)
                    val imageRequest = Request.Builder()
                        .url("https://api.openai.com/v1/images/generations")
                        .method("POST", body)
                        .addHeader("Authorization", "Bearer $oPENAI_API_KEY")
                        .build()

                    val response = client.newCall(imageRequest).execute()
                    println(response)

                    if (response.isSuccessful && response.body != null) {
                        val responseString = response.body!!.string()
                        return@withContext JSONUtil.getUrlFromJson(responseString)
                    } else {
                        throw IOException("Failed to get response from API: ${response.code}")
                    }
                }

                if (!responseUrl.isNullOrEmpty()) {
                    println(responseUrl)
                    binding.dalleImage.visibility = View.VISIBLE

                    binding.dalleImage.load(responseUrl)

                }

            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(
                    requireContext(),
                    "인터넷 연결을 다시 확인해주세요. ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: JSONException) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "이미지를 생성할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }

        }

        lifecycleScope.launch {
            try {
                val responseText = withContext(Dispatchers.IO) {
                    val jsonString = """
                    {
                        "prompt": "Please perform the creation theme function of a text adventure game, following the rules listed below:

                                presentation rules
                                
                                1. The game output will always show 'title', 'theme' and 'story summary'
                                
                                2. Wrap all game output in code blocks.
                                
                                3. The subject is $topic, please write the subject in a little more detail you want so that it can be used as a text adventure game.
                                
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
                    binding.summaryText.visibility = View.VISIBLE
                    binding.summaryText.text = responseText
                    Log.d(TAG, "텍스트 생성 성공: $responseText") // 로그로 출력

                    binding.button1.visibility = View.VISIBLE
                    binding.button2.visibility = View.VISIBLE

                }
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(
                    requireContext(),
                    "인터넷 연결을 다시 확인해주세요. ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: JSONException) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "텍스트를 생성할 수 없습니다.", Toast.LENGTH_SHORT).show()
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








