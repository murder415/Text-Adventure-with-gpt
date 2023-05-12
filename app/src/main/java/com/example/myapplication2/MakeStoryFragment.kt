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


data class ImageResponse(val data: List<ImageData>?) // 'data' 타입을 nullable로 변경합니다.data class ImageData(val url: String)
data class ImageData(val url: String)
data class StoryResponse(val choices: List<StoryChoice>)
data class StoryChoice(val text: String)



class MakeStoryFragment : Fragment() {

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
            binding.button1.visibility = View.VISIBLE
            binding.button2.visibility = View.VISIBLE

        }
    }

    private fun fetchStory(topic: String) {
        lifecycleScope.launch {
            try {
                val responseUrl = withContext(Dispatchers.IO) {

                    val jsonString = """
                        {
                            "model": "image-alpha-001",
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


        val storyRequest = Request.Builder()
            .url("https://api.openai.com/v1/engines/text-davinci-002/completions")
            .post(
                "{\"prompt\": \"$topic 에 대한 텍스트 어드벤처 게임의 핵심 스토리 라인을 10줄 이내로 작성해줘 \",\n\"temperature\": 0.7,\n\"max_tokens\": 60,\n\"n\": 1,\n\"stop\": \"\\n\\n\"}"
                    .toRequestBody("application/json".toMediaType())
            )
            .addHeader("Authorization", "Bearer $oPENAI_API_KEY")
            .build()



        client.newCall(storyRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle failure
                Log.e(tAG, "Failed to generate story", e)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()

                try {
                    val storyResponse = storyResponseAdapter.fromJson(responseBody!!)
                    if (storyResponse?.choices.isNullOrEmpty()) {
                        throw JsonDataException("Unable to parse story response")
                    }

                    val storyText =
                        storyResponse?.choices?.get(0)?.text // Use '?.' operators instead of '!!.'

                    activity?.runOnUiThread {
                        binding.summaryText.text = storyText

                    }
                } catch (e: Exception) {
                    Log.e(tAG, "Failed to parse story response", e)
                }
            }


        })

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

