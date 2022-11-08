package io.usys.report.ui.reviewSystem

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import io.usys.report.R
import io.usys.report.utils.*

class YsrQuestionCard(context: Context) : CardView(context) {
    constructor(context: Context, attrs: AttributeSet) : this(context)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : this(context)

    companion object {
        const val A = "a"
        const val B = "b"
        const val C = "c"
        const val D = "d"
    }

    private var mainLayout: ConstraintLayout? = null
    private var question: TextView? = null
    private var choiceA: RadioButton? = null
    private var choiceB: RadioButton? = null
    private var choiceC: RadioButton? = null
    private var choiceD: RadioButton? = null

    override fun onViewAdded(child: View?) {
//        bindChildren()
//        setupRadioListeners()
    }

//    private fun bindChildren() {
//        mainLayout = this.rootView.bind(R.id.cardQuestionMainLayout)
//        question = this.rootView.bind(R.id.cardQuestionTxtQ)
//        choiceA = this.rootView.bind(R.id.cardQuestionRadioA)
//        choiceB = this.rootView.bind(R.id.cardQuestionRadioB)
//        choiceC = this.rootView.bind(R.id.cardQuestionRadioC)
//        choiceD = this.rootView.bind(R.id.cardQuestionRadioD)
//    }

//   private fun setupRadioListeners() {
//
//       choiceA?.setOnCheckedChangeListener { _,_ ->
//           toggleRadioOn(A)
//       }
//       choiceB?.setOnCheckedChangeListener { _,_ ->
//           toggleRadioOn(B)
//       }
//       choiceC?.setOnCheckedChangeListener { _,_ ->
//           toggleRadioOn(C)
//       }
//       choiceD?.setOnCheckedChangeListener { _,_ ->
//           toggleRadioOn(D)
//       }
//
//   }

//    private fun toggleRadioOn(letter:String) {
//        when (letter) {
//            A -> {
//                choiceA?.isChecked = true
//                choiceB?.isChecked = false
//                choiceC?.isChecked = false
//                choiceD?.isChecked = false
//            }
//            B -> {
//                choiceA?.isChecked = false
//                choiceB?.isChecked = true
//                choiceC?.isChecked = false
//                choiceD?.isChecked = false
//            }
//            C -> {
//                choiceA?.isChecked = false
//                choiceB?.isChecked = false
//                choiceC?.isChecked = true
//                choiceD?.isChecked = false
//            }
//            D -> {
//                choiceA?.isChecked = false
//                choiceB?.isChecked = false
//                choiceC?.isChecked = false
//                choiceD?.isChecked = true
//            }
//        }
//    }

    private fun getChoiceScore(letter:String) : Int {
        return when (letter) {
            A -> 5
            B -> 4
            C -> 3
            D -> 2
            else -> 1
        }
    }
}