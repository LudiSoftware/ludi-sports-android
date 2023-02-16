package io.usys.report.ui.ysr.review.coach

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmList
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.firebase.FireTypes
import io.usys.report.firebase.fireGetReviewTemplateQuestionsAsync
import io.usys.report.realm.model.*
import io.usys.report.ui.onClickReturnEmpty
import io.usys.report.ui.onClickReturnStringString
import io.usys.report.ui.onClickReturnViewRealmObject
import io.usys.report.realm.loadInRealmListCallback
import io.usys.report.realm.model.users.safeUserId
import io.usys.report.realm.toRealmList
import io.usys.report.ui.ysr.review.engine.*
import io.usys.report.utils.*

class YsrCoachReviewView(context: Context, attrs: AttributeSet) : CardView(context, attrs) {
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : this(context, attrs)

    companion object {
        const val A = "a"
        const val B = "b"
        const val C = "c"
        const val D = "d"
    }

    var itemOnClick: ((View, RealmObject) -> Unit)? = onClickReturnViewRealmObject()
    var updateCallback: ((String, String) -> Unit)? = onClickReturnStringString()
    var finishCallback: (() -> Unit)? = onClickReturnEmpty()
    private var mainLayout: ConstraintLayout? = null
    private var title: TextView? = null
    private var recyclerView: RecyclerView? = null
    private var btnCancel: Button? = null
    private var btnSubmit: Button? = null

    private var reviewTemplate: ReviewTemplate? = null
    private var reviewQuestions: RealmList<Question>? = null
    var currentCoach: Coach? = null

    var reviewScore = ""
    var reviewCount = ""
    var reviewAnswerCount = ""

    override fun onViewAdded(child: View?) {
        bindChildren()
        setupRadioListeners()

        //TODO: LOAD THIS AT THE BEGINNING, SAVE IT IN REALM FOR GLOBAL USE!
        // Review Template
//        getReviewTemplateAsync(FireTypes.COACHES) {
//            log("reviewTemplate successfully pulled.")
//            reviewTemplate = this?.getValue(ReviewTemplate::class.java)
//        }
        fireGetReviewTemplateQuestionsAsync(FireTypes.COACHES) {
            log("reviewTemplate successfully pulled.")
            reviewQuestions = this?.toRealmList()
            recyclerView?.loadInRealmListCallback(reviewQuestions, context, FireTypes.REVIEW_TEMPLATES, updateCallback)
        }

        updater()
    }


    private fun updater() {
        updateCallback = { question, score ->

            reviewQuestions?.let {
                for (q in it) {
                    if (q.question == question) {
                        q.finalScore = score
                    }
                }
            }

            log(question)
            log(score)
        }
    }

    private fun bindChildren() {
        mainLayout = this.rootView.bind(R.id.reviewMainLayout)
        title = this.rootView.bind(R.id.reviewTxtTitle)
        recyclerView = this.rootView.bind(R.id.reviewCoachRecyclerView)
        btnSubmit = this.rootView.bind(R.id.reviewBtnSubmit)
        btnCancel = this.rootView.bind(R.id.reviewBtnCancel)
    }

    private fun generateRatingScoreCount() {
        var tempScore = 0
        var tempCount = 0
        var tempAnswerCount = 0

        reviewQuestions?.let {
            for (q in it) {
                if (q.finalScore != "0") {
                    tempScore += q.finalScore.toInt()
                    tempAnswerCount += 1
                }

            }
            tempCount += 1
        }


        currentCoach?.let {
            reviewCount = (tempCount + it.reviewBundle?.ratingCount?.toInt()!!).toString()
            reviewAnswerCount = (tempAnswerCount + it.reviewBundle?.reviewAnswerCount?.toInt()!!).toString()
            val tempRating = tempScore / tempAnswerCount
            reviewScore = calculateAverageRatingScore(it.reviewBundle?.ratingScore, tempRating.toFloat())
        }

    }

   private fun setupRadioListeners() {
       log("btnSubmit")
       //TODO: not complete yet!
       btnSubmit?.setOnClickListener {
           generateRatingScoreCount()
           safeUserId { itUserId ->
               currentCoach?.let { itCoach ->
                   Review().apply {
                       this.creatorId = itUserId
                       this.receiverId = itCoach.coachId
                       this.sportName = "soccer"
                       this.type = FireTypes.COACHES
                       this.questions = reviewQuestions
                       this.score = reviewScore
                   }.fireAddUpdateToDB()
                   updateCoachRatingScore(itCoach.coachId, reviewScore)
                   updateCoachReviewCount(itCoach.coachId, reviewCount)
                   updateCoachReviewAnswerCount(itCoach.coachId, reviewAnswerCount)
               }
           }
           finishCallback?.invoke()
       }

       itemOnClick = { _,_ ->
           log("item on click")
       }

   }


}