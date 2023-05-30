package com.example.myapplication2

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class ChoiceFragment : Fragment() {
    private lateinit var command1Button: Button
    private lateinit var command2Button: Button
    private lateinit var command3Button: Button

    // commands 변수 선언
    private lateinit var commands: Map<String, String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.choice_fragment, container, false)

        command1Button = view.findViewById(R.id.command1Button)
        command2Button = view.findViewById(R.id.command2Button)
        command3Button = view.findViewById(R.id.command3Button)

        // StoryFragment에서 전달된 commands 값을 가져옴 (수정이 필요함)
        // commands = ??? (StoryFragment에서 전달된 값 가져오기)

        // commands의 첫 번째 키의 값을 첫 번째 버튼에 설정
        command1Button.text = commands.values.firstOrNull()

        // commands의 두 번째 키의 값을 두 번째 버튼에 설정
        val secondCommandValue = commands.values.drop(1).firstOrNull()
        command2Button.text = secondCommandValue

        // commands의 세 번째 키의 값을 세 번째 버튼에 설정
        val thirdCommandValue = commands.values.drop(2).firstOrNull()
        command3Button.text = thirdCommandValue

        // 첫 번째 버튼 클릭 시 처리
        command1Button.setOnClickListener {
            val message = messages[commands.keys.firstOrNull()] // 수정이 필요함
            navigateToNextFragment(message)
        }

        // 두 번째 버튼 클릭 시 처리
        command2Button.setOnClickListener {
            val message = messages[commands.keys.drop(1).firstOrNull()] // 수정이 필요함
            navigateToNextFragment(message)
        }

        // 세 번째 버튼 클릭 시 처리
        command3Button.setOnClickListener {
            showEditCommandsDialog()
        }

        return view
    }

    // 다음 Fragment로 이동하는 함수
    private fun navigateToNextFragment(message: String?) {
        val action = ChoiceFragmentDirections.actionChoiceFragmentToStoryFragment(message)
        findNavController().navigate(action)
    }

    // commands 수정 다이얼로그를 표시하는 함수
    private fun showEditCommandsDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.edit_commands_dialog, null)
        builder.setView(dialogView)
            .setPositiveButton("확인") { dialog, _ ->
                val newCommand = dialogView.findViewById<EditText>(R.id.editCommandsEditText).text.toString()
                // commands를 수정하고 저장 (수정이 필요함)
                // commands[commands.keys.drop(2).firstOrNull()] = newCommand
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .setTitle("Commands 수정")
            .setMessage("주의사항: 한 번 입력하면 절대 바꿀 수 없습니다.")
            .show()
    }
}
