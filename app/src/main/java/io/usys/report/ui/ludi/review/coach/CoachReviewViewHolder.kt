package io.usys.report.ui.ludi.review.coach

import android.content.Context
import android.view.View
import android.widget.RadioButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmList
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.firebase.FireTypes
import io.usys.report.realm.model.Question
import io.usys.report.realm.model.getQuestionScore
import io.usys.report.ui.views.listAdapters.loadInCustomAttributes
import io.usys.report.ui.ludi.review.coach.YsrCoachReviewView.Companion.A
import io.usys.report.ui.ludi.review.coach.YsrCoachReviewView.Companion.B
import io.usys.report.ui.ludi.review.coach.YsrCoachReviewView.Companion.C
import io.usys.report.ui.ludi.review.coach.YsrCoachReviewView.Companion.D
import io.usys.report.utils.bind
import io.usys.report.utils.log

/**
 * REVIEW TEMPLATE COACH LIST VIEW CONTROLS
 *
 * Create Callbacks from each card that the Fragment uses to update the overall Review Object
 */

fun RecyclerView?.setupReviewQuestionsList(context: Context, questionList: RealmList<Question>?, itemOnClick: ((View, RealmObject) -> Unit)?) {
    // Load Reviews by Receiver.id
    val rv = this
    rv?.loadInCustomAttributes(questionList, FireTypes.REVIEW_TEMPLATES, itemOnClick)
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
            cardQuestionTxtQ?.text = it.question
            cardQuestionRadioA?.text = it.choiceA
            cardQuestionRadioB?.text = it.choiceB
            cardQuestionRadioC?.text = it.choiceC
            cardQuestionRadioD?.text = it.choiceD

            cardQuestionRadioA?.setOnClickListener {
                toggleRadioOn(A)
                currentChoice = A
                updater()
            }
            cardQuestionRadioB?.setOnClickListener {
                toggleRadioOn(B)
                currentChoice = B
                updater()
            }
            cardQuestionRadioC?.setOnClickListener {
                toggleRadioOn(C)
                currentChoice = C
                updater()
            }
            cardQuestionRadioD?.setOnClickListener {
                toggleRadioOn(D)
                currentChoice = D
                updater()
            }
        }

    }

    private fun toggleRadioOn(letter:String) {
        when (letter) {
            A -> {
                cardQuestionRadioA?.isChecked = true
                cardQuestionRadioB?.isChecked = false
                cardQuestionRadioC?.isChecked = false
                cardQuestionRadioD?.isChecked = false
            }
            B -> {
                cardQuestionRadioA?.isChecked = false
                cardQuestionRadioB?.isChecked = true
                cardQuestionRadioC?.isChecked = false
                cardQuestionRadioD?.isChecked = false
            }
            C -> {
                cardQuestionRadioA?.isChecked = false
                cardQuestionRadioB?.isChecked = false
                cardQuestionRadioC?.isChecked = true
                cardQuestionRadioD?.isChecked = false
            }
            D -> {
                cardQuestionRadioA?.isChecked = false
                cardQuestionRadioB?.isChecked = false
                cardQuestionRadioC?.isChecked = false
                cardQuestionRadioD?.isChecked = true
            }
        }
    }
}
