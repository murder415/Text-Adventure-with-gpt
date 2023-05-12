/*package com.example.myapplication2


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication2.ChatViewModel
import com.example.myapplication2.databinding.StoryFragmentBinding

class StoryFragment : Fragment() {

    private lateinit var binding: StoryFragmentBinding
    private val chatViewModel by activityViewModels<ChatViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = StoryFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.storyInventoryButton.setOnClickListener {
            val action = StoryFragmentDirections.actionStoryFragmentToInventoryFragment()
            findNavController().navigate(action)
        }

        binding.storyChatButton.setOnClickListener {
            val action = StoryFragmentDirections.actionStoryFragmentToChatBotFragment()
            findNavController().navigate(action)
        }

        val adapter = MyAdapter()
        binding.storyViewPager.adapter = adapter

        chatViewModel.messageResponse.observe(viewLifecycleOwner) { response ->
            response?.let {
                val message = it.choices.firstOrNull()?.text
                adapter.addMessage(message)
                binding.storyTextView.typeText(message)
            }
        }

        chatViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), "Error occurred: $it", Toast.LENGTH_SHORT).show()
            }
        }

        val apiKey = "YOUR_API_KEY_HERE"
        chatViewModel.setApiKey(apiKey)

        val messageRequest = MessageRequest("Hello!", 4, 100)
        chatViewModel.sendMessage(messageRequest)
    }

    inner class MyAdapter : RecyclerView.Adapter<MyViewHolder>() {

        private val messages = mutableListOf<String>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.view_holder_story, parent, false)
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val message = messages[position]
            holder.itemView.findViewById<TextView>(R.id.storyTextView).text = message
        }

        override fun getItemCount(): Int {
            return messages.size
        }

        fun addMessage(message: String?) {
            message?.let {
                messages.add(it)
                notifyDataSetChanged()
            }
        }
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private fun TextView.typeText(text: String?) {
        text?.let {
            this.text = null
            val delayMillis = 50L
            text.forEachIndexed { index, char ->
                postDelayed({
                    append(char)
                }, delayMillis * index)
            }
        }
    }
}
*/

