/* package com.example.myapplication2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication2.ChatGPTService
import com.example.myapplication2.MessageRequest
import com.example.myapplication2.MessageResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception


class ChatViewModel : ViewModel() {
    private val service = Retrofit.Builder()
        .baseUrl("https://api.openai.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ChatGPTService::class.java)

    private val _messages = MutableLiveData<List<String>>()
    val messages: LiveData<List<String>> = _messages

    fun sendMessage(text: String) {
        viewModelScope.launch {
            try {
                val response = service.getMessage(MessageRequest(text, 1, 50))
                val result = response.choices[0].text
                val lines = result.split("\n")
                _messages.value = lines
            } catch (e: Exception) {
                _messages.value = listOf("Error: ${e.message}")
            }
        }
    }
}


 */