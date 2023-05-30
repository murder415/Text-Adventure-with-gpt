package com.example.myapplication2

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.chaquo.python.Python
import android.app.Dialog
import android.view.MotionEvent
import android.view.Window
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2


class ChoiceFragment : Fragment() {
    private lateinit var command1Button: Button
    private lateinit var command2Button: Button
    private lateinit var command3Button: Button
    private lateinit var choiceButton: ImageButton

    private var translatedCommands: Map<String, String> = emptyMap()

    private  lateinit var viewPager: ViewPager2

    // commands 변수 선언
    private var commands: Map<String, String> = emptyMap()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.choice_fragment, container, false)


        command1Button = view.findViewById(R.id.button1)
        command2Button = view.findViewById(R.id.button2)
        command3Button = view.findViewById(R.id.button3)
        choiceButton = view.findViewById(R.id.chocieButton)

        choiceButton.visibility = View.VISIBLE



        val py = Python.getInstance()
        val pyObject = py.getModule("translate")

        // StoryFragment에서 전달된 commands 값을 가져옴 (수정이 필요함)
        // commands = ??? (StoryFragment에서 전달된 값 가져오기)
        val translatedCommands = commands.mapValues { (_, value) ->
            pyObject.callAttr("en2ko", value).toString()
        }.toMutableMap()

        // commands의 첫 번째 키의 값을 첫 번째 버튼에 설정
        command1Button.text = translatedCommands.values.elementAtOrNull<String>(0)

// commands의 두 번째 키의 값을 두 번째 버튼에 설정
        command2Button.text = translatedCommands.values.elementAtOrNull<String>(1)

// commands의 세 번째 키의 값을 세 번째 버튼에 설정
        val thirdCommandValue = translatedCommands.values.elementAtOrNull<String>(2)
        if (commands.containsKey("3")) {
            choiceButton.visibility = View.VISIBLE
            command3Button.isEnabled = true
            command3Button.visibility = View.VISIBLE
        } else {
            command3Button.isEnabled = false
            command3Button.visibility = View.GONE
        }


//        // commands의 첫 번째 키의 값을 첫 번째 버튼에 설정
//        command1Button.text = commands.values.firstOrNull()
//
//        // commands의 두 번째 키의 값을 두 번째 버튼에 설정
//        val secondCommandValue = commands.values.drop(1).firstOrNull()
//        command2Button.text = secondCommandValue
//
//        // commands의 세 번째 키의 값을 세 번째 버튼에 설정
//        val thirdCommandValue = commands.values.drop(2).firstOrNull()
//        command3Button.text = thirdCommandValue

        // 첫 번째 버튼 클릭 시 처리
        command1Button.setOnClickListener {

        }

        // 두 번째 버튼 클릭 시 처리
        command2Button.setOnClickListener {

        }

        // 세 번째 버튼 클릭 시 처리
        command3Button.setOnClickListener {
            showEditCommandsDialog()
        }

        choiceButton.setOnClickListener{

        }

        return view
    }


    fun updatePossibleCommands(newPossibleCommands: Map<String, String>) {
        commands = newPossibleCommands
    }
    // 다음 Fragment로 이동하는 함수


    // commands 수정 다이얼로그를 표시하는 함수
    private fun showEditCommandsDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.edit_commands_dialog) // 커스텀 다이얼로그 레이아웃 설정

        val editCommandsEditText = dialog.findViewById<EditText>(R.id.editCommandsEditText)
        val btnConfirm = dialog.findViewById<Button>(R.id.btnConfirm)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)

        dialog.setCancelable(false) // 다이얼로그가 취소되지 않도록 설정

        dialog.show()

        btnConfirm.setOnClickListener{
            val newCommand = editCommandsEditText.text.toString()
            val confirmDialog = AlertDialog.Builder(requireContext())
                .setTitle("확인")
                .setMessage("당신의 선택을 후회할 때가 올 수도 있습니다. 이대로 가시겠습니까?")
                .setPositiveButton("예") { _, _ ->
                    command3Button.text = newCommand
                    dialog.dismiss()
                }
                .setNegativeButton("아니요") { _, _ ->
                    // 아무 작업도 수행하지 않고 대화 상자를 닫습니다.
                }
                .setCancelable(false)
                .create()

            confirmDialog.show()
        }

        btnCancel.setOnClickListener{
            dialog.dismiss()
        }

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

//        val builder = AlertDialog.Builder(requireContext())
//        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.edit_commands_dialog, null)
//        builder.setView(dialogView)
//            .setPositiveButton("확인") { dialog, _ ->
//                val newCommand = dialogView.findViewById<EditText>(R.id.editCommandsEditText).text.toString()
//                command3Button.text = newCommand
//                dialog.dismiss()
//            }
//            .setNegativeButton("취소") { dialog, _ ->
//                dialog.dismiss()
//            }
//            .setTitle("Commands 수정")
//            .setMessage("주의사항: 한 번 입력하면 절대 바꿀 수 없습니다.")
//            .show()
    }

    private fun showChoiceDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.choice_dialog, null)
        val dialogButton = dialogView.findViewById<Button>(R.id.dialogButton)

        val thirdCommandValue = translatedCommands.values.elementAtOrNull<String>(2)
        if (commands.containsKey("3")) {
            dialogButton.text = thirdCommandValue
            dialogButton.isEnabled = true
            dialogButton.visibility = View.VISIBLE
        } else {
            dialogButton.text = "아직 세상을 바꿀 힘은 충분하지 않나 봅니다."
            dialogButton.isEnabled = false
            dialogButton.visibility = View.GONE
        }

        val dialog = builder.setView(dialogView)
            .setCancelable(false)
            .setTitle("Choice")
            .show()

        dialog.apply {
            setOnCancelListener {
                dismiss()
            }

            getButton(AlertDialog.BUTTON_POSITIVE).visibility = View.GONE
            getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
                dismiss()
            }
        }

        dialogButton.setOnClickListener {
            // dialogButton 클릭에 대한 처리
        }
    }

}
