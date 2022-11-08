package io.usys.report.ui.reviewSystem

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
import io.usys.report.firebase.FireDB
import io.usys.report.firebase.FireTypes
import io.usys.report.firebase.getReviewTemplateAsync
import io.usys.report.firebase.getReviewTemplateQuestionsAsync
import io.usys.report.model.*
import io.usys.report.ui.loadInRealmListCallback
import io.usys.report.utils.*

class YsrCoachReview(context: Context, attrs: AttributeSet) : CardView(context, attrs) {
//    constructor(context: Context, attrs: AttributeSet) : this(context)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : this(context, attrs)

    var itemOnClick: ((View, RealmObject) -> Unit)? = null
    var updateCallback: ((String, String) -> Unit)? = null
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
        getReviewTemplateAsync(FireTypes.COACHES) {
            log("reviewTemplate successfully pulled.")
            reviewTemplate = this?.getValue(ReviewTemplate::class.java)
        }
        getReviewTemplateQuestionsAsync(FireTypes.COACHES) {
            log("reviewTemplate successfully pulled.")
            reviewQuestions = this?.toRealmList()
            recyclerView?.loadInRealmListCallback(reviewQuestions, context, FireDB.REVIEW_TEMPLATES, updateCallback)
        }

        updater()
    }


    private fun updater() {
        updateCallback = { question, score ->

            for (q in reviewQuestions!!) {
                if (q.question == question) {
                    q.finalScore = score
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

    fun generateRatingScoreCount() {
        var tempScore = 0
        var tempCount = 0
        var tempAnswerCount = 0

        for (q in reviewQuestions!!) {
            if (q.finalScore != "0") {
                tempScore += q.finalScore.toInt()
                tempAnswerCount += 1
            }
            tempCount += 1
        }

        reviewScore = tempScore.toString()
        reviewCount = tempCount.toString()
        reviewAnswerCount = tempAnswerCount.toString()
    }

   private fun setupRadioListeners() {

       btnSubmit?.setOnClickListener {
            generateRatingScoreCount()
           //TODO: GRAB ALL QUESTIONS
           //- BUILD REVIEW OBJECT..
           safeUserId { itUserId ->
               currentCoach?.let { itCoach ->
                   Review().apply {
                       this.creatorId = itUserId
                       this.receiverId = itCoach.id
                       this.sportName = "soccer"
                       this.type = FireTypes.COACHES
                       this.questions = reviewQuestions
                   }.addUpdateInFirebase()
                   updateCoachRatingScore(itCoach.id, reviewScore)
                   updateCoachReviewCount(itCoach.id, reviewCount)
                   updateCoachReviewAnswerCount(itCoach.id, reviewAnswerCount)
               }



               //update coaches review count and score


               //save review

           }



           log("btnSubmit")
       }
//       btnCancel?.setOnClickListener {
//           log("btnCancel")
//       }

       itemOnClick = { _,_ ->
           log("item on click")
       }

   }


}