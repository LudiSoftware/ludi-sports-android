package io.usys.report.ui.reviewSystem

import android.content.Context
import android.view.View
import android.widget.RadioButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import io.realm.RealmList
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.firebase.FireTypes
import io.usys.report.firebase.getReviewsByReceiverIdToCallback
import io.usys.report.model.Question
import io.usys.report.model.Review
import io.usys.report.model.getQuestionScore
import io.usys.report.ui.loadInRealmList
import io.usys.report.utils.bind
import io.usys.report.utils.log
import io.usys.report.utils.toRealmList

/**
 * REVIEW TEMPLATE COACH LIST VIEW CONTROLS
 *
 * Create Callbacks from each card that the Fragment uses to update the overall Review Object
 */

fun RecyclerView?.setupReviewQuestionsList(context: Context, questionList: RealmList<Question>?, itemOnClick: ((View, RealmObject) -> Unit)?) {
    // Load Reviews by Receiver.id
    val rv = this
    rv?.loadInRealmList(questionList, context, FireTypes.REVIEW_TEMPLATES, itemOnClick)

}

class ReviewQuestionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    constructor(itemView: View, callbackUpdater:((String, String) -> Unit)?=null) : this(itemView) {
        this.callbackUpdater = callbackUpdater
    }

    var callbackUpdater: ((String,String) -> Unit)? = null

    val cardQuestionMainLayout = itemView.bind<ConstraintLayout>(R.id.cardQuestionMainLayout)
    val cardQuestionTxtQ = itemView.bind<TextView>(R.id.cardQuestionTxtQ)
    val cardQuestionRadioA = itemView.bind<RadioButton>(R.id.cardQuestionRadioA)
    val cardQuestionRadioB = itemView.bind<RadioButton>(R.id.cardQuestionRadioB)
    val cardQuestionRadioC = itemView.bind<RadioButton>(R.id.cardQuestionRadioC)
    val cardQuestionRadioD = itemView.bind<RadioButton>(R.id.cardQuestionRadioD)

    var currentQuestion: String = ""
    var currentQuestionId: String = ""
    var currentChoice: String? = null

    private fun getScore() : String {
        return getQuestionScore(currentChoice)
    }

    private fun updater() {
        log("updater")
        callbackUpdater?.invoke(currentQuestion, getScore())
    }

    fun bind(questions: Question?) {
        questions?.let {
            currentQuestion = it.question
            cardQuestionTxtQ.text = it.question
            cardQuestionRadioA.text = it.choiceA
            cardQuestionRadioB.text = it.choiceB
            cardQuestionRadioC.text = it.choiceC
            cardQuestionRadioD.text = it.choiceD

            cardQuestionRadioA.setOnClickListener {
                toggleRadioOn(YsrQuestionCard.A)
                currentChoice = YsrQuestionCard.A
                updater()
            }
            cardQuestionRadioB.setOnClickListener {
                toggleRadioOn(YsrQuestionCard.B)
                currentChoice = YsrQuestionCard.B
                updater()
            }
            cardQuestionRadioC.setOnClickListener {
                toggleRadioOn(YsrQuestionCard.C)
                currentChoice = YsrQuestionCard.C
                updater()
            }
            cardQuestionRadioD.setOnClickListener {
                toggleRadioOn(YsrQuestionCard.D)
                currentChoice = YsrQuestionCard.D
                updater()
            }
        }

    }

    private fun toggleRadioOn(letter:String) {
        when (letter) {
            YsrQuestionCard.A -> {
                cardQuestionRadioA.isChecked = true
                cardQuestionRadioB.isChecked = false
                cardQuestionRadioC.isChecked = false
                cardQuestionRadioD.isChecked = false
            }
            YsrQuestionCard.B -> {
                cardQuestionRadioA.isChecked = false
                cardQuestionRadioB.isChecked = true
                cardQuestionRadioC.isChecked = false
                cardQuestionRadioD.isChecked = false
            }
            YsrQuestionCard.C -> {
                cardQuestionRadioA.isChecked = false
                cardQuestionRadioB.isChecked = false
                cardQuestionRadioC.isChecked = true
                cardQuestionRadioD.isChecked = false
            }
            YsrQuestionCard.D -> {
                cardQuestionRadioA.isChecked = false
                cardQuestionRadioB.isChecked = false
                cardQuestionRadioC.isChecked = false
                cardQuestionRadioD.isChecked = true
            }
        }
    }
}
