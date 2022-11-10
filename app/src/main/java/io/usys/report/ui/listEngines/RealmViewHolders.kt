package io.usys.report.ui

import android.content.Context
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import io.realm.RealmList
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.firebase.*
import io.usys.report.model.*
import io.usys.report.ui.reviewSystem.ReviewQuestionsViewHolder
import io.usys.report.utils.*

/**
 * This Class will 'route' the RecyclerView to the correct ViewHolder based on its 'type'.
 */
class RouterViewHolder(itemView: View, var type:String, var updateCallback:((String, String) -> Unit)?=null) : RecyclerView.ViewHolder(itemView) {

    /**     Router / RecyclerView Checklist.
     * 1. Realm Model of FireType
     * 2. Card Layout for ListItem
     * 3. ViewModel
     * 4. Add FireType to Router bind()
     * 5. Add Card Layout to getLayout() method.
     * - Create Convenience Ext Method if Wanted.
     */
    fun bind(obj: RealmObject) {
        when (type) {
            FireTypes.SPORTS -> return SportViewHolder(itemView).bind(obj as? Sport)
            FireTypes.ORGANIZATIONS -> return OrgViewHolder(itemView).bind(obj as? Organization)
            FireTypes.COACHES -> return CoachViewHolder(itemView).bind(obj as? Coach)
            FireTypes.SERVICES -> return ServiceViewHolder(itemView).bind(obj as? Service)
            FireTypes.REVIEWS -> return ReviewViewHolder(itemView).bind(obj as? Review)
            FireTypes.REVIEW_TEMPLATES -> return ReviewQuestionsViewHolder(itemView, updateCallback).bind(obj as? Question)
        }
    }

    companion object {
        fun getLayout(type: String): Int {
            return when (type) {
                FireTypes.ORGANIZATIONS -> R.layout.card_organization
                FireTypes.SPORTS -> R.layout.card_sport
                FireTypes.REVIEWS -> R.layout.card_review_comment
                FireTypes.USERS -> R.layout.card_sport
                FireTypes.COACHES -> R.layout.card_coach
                FireTypes.SERVICES -> R.layout.card_service
                FireTypes.REVIEW_TEMPLATES -> R.layout.card_review_question
                else -> R.layout.card_sport
            }
        }
    }

}
/**
 * SPORT LIST VIEW CONTROLS
 */

fun RecyclerView?.setupSportList(context: Context, onClick: ((View, RealmObject) -> Unit)?) {
    // Load Organizations by Sport
    val rv = this
    var sportList: RealmList<Sport>? = RealmList()
    fireGetBaseYsrObjects<Sport>(FireTypes.SPORTS) {
        executeRealm {
            sportList = this ?: RealmList()
            sportList.addToSession()
        }
        rv?.loadInRealmList(sportList, context, FireTypes.SPORTS, onClick)
    }
}

class SportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var txtItemSportName = itemView.bindTextView(R.id.cardSportTxtSportName)

    fun bind(sport: Sport?) {
        sport?.let { txtItemSportName?.text = it.name }
    }
}

/**
 * ORGANIZATION LIST VIEW CONTROLS
 */

fun RecyclerView?.setupOrganizationList(context: Context, realmObjectArg: RealmObject?, onClick: ((View, RealmObject) -> Unit)? ) {
    // Load Organizations by Sport.name
    val rv = this
    var organizationList: RealmList<Organization>?
    fireGetOrderByEqualToAsync(FireTypes.ORGANIZATIONS, Organization.ORDER_BY_SPORTS, realmObjectArg?.cast<Sport>()?.name!!) {
        organizationList = this?.toRealmList()
        rv?.loadInRealmList(organizationList, context, FireTypes.ORGANIZATIONS, onClick)
    }
}

class OrgViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

//    var itemLinearLayout = itemView.bindLinearLayout(R.id.itemLinearLayout)
    var txtOrgName = itemView.bindTextView(R.id.cardOrgTxtName)
    var txtWebsite = itemView.bindTextView(R.id.cardOrgTxtWebsite)
    var txtLeague = itemView.bindTextView(R.id.cardOrgTxtLeague)
    var imgOrganization: ImageView = itemView.bind(R.id.cardOrgImgOrg)
    var includeReviewCard: FrameLayout = itemView.findViewById(R.id.includeReviewCard)

    var cardRatingBar: RatingBar = includeReviewCard.bind(R.id.cardRatingBar)
    var cardRatingBarScore: TextView = includeReviewCard.bind(R.id.cardRatingTxtScore)
    var cardRatingBarCount: TextView = includeReviewCard.bind(R.id.cardRatingTxtCount)

    fun bind(org: Organization?) {
        txtOrgName?.text = org?.name
        txtWebsite?.text = org?.websiteUrl ?: "www.usysr.io"
        txtLeague?.text = org?.leagueIds?.first() ?: "Alabama State League"
        cardRatingBar.rating = org?.ratingScore?.toFloat() ?: 0.0F
        cardRatingBarScore.text = org?.ratingScore
        cardRatingBarCount.text = "${org?.ratingCount} Reviews"
    }
}

/**
 * COACH LIST VIEW CONTROLS
 */

fun RecyclerView?.setupCoachList(context: Context, realmObjectArg: RealmObject?, onClick: ((View, RealmObject) -> Unit)? ) {
    // Load Coaches by Organization.id
    val rv = this
    var coachesList: RealmList<Coach>?
    fireGetOrderByEqualToAsync(FireTypes.COACHES, Coach.ORDER_BY_ORGANIZATION, realmObjectArg?.cast<Organization>()?.id ?: return) {
        coachesList = this?.toRealmList()
        rv?.loadInRealmList(coachesList, context, FireTypes.COACHES, onClick)
    }
}

class CoachViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var cardCoachTxtName = itemView.bindTextView(R.id.cardCoachTxtName)
    var cardCoachImgProfile = itemView.bind<ImageView>(R.id.cardCoachImgProfile)
    var cardCoachTxtOrg = itemView.bindTextView(R.id.cardCoachTxtOrg)
    var cardCoachTxtEmail = itemView.bindTextView(R.id.cardCoachTxtEmail)

    fun bind(coach: Coach?) {
        coach?.let {
            cardCoachTxtName?.text = it.name
            cardCoachTxtEmail?.text = it.email
            cardCoachTxtOrg?.text = it.organizationName
            itemView.context.loadUriIntoImgView(it.imgUrl.toUri() ?: return, cardCoachImgProfile)
        }
    }
}

/**
 * REVIEW LIST VIEW CONTROLS
 */

fun RecyclerView?.setupReviewList(context: Context, receiverId:String, itemOnClick: ((View, RealmObject) -> Unit)?) {
    // Load Reviews by Receiver.id
    val rv = this
    val callBack : ((DataSnapshot?) -> Unit) = { ds ->
        val reviewList = ds?.toRealmList<Review>()
        rv?.loadInRealmList(reviewList, context, FireTypes.REVIEWS, itemOnClick)
    }
    fireGetReviewsByReceiverIdToCallback(receiverId, callBack)
}

class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var cardReviewTxtDateCreated = itemView.bindTextView(R.id.cardReviewTxtDateCreated)
    private var cardReviewTxtComment = itemView.bindTextView(R.id.cardReviewTxtComment)

    fun bind(review: Review?) {
        review?.let {
            cardReviewTxtDateCreated?.text = it.dateCreated
            cardReviewTxtComment?.text = it.comment
        }
    }
}


/**
 * SERVICES LIST VIEW CONTROLS
 */

fun RecyclerView?.setupServiceList(context: Context, onClick: ((View, RealmObject) -> Unit)?) {
    // Load Organizations by Sport
    val rv = this
    Session.session?.let {
        log(it)
        if (!it.services.isNullOrEmpty()) {
            rv?.loadInRealmListHorizontal(it.services, context, FireTypes.SERVICES, onClick)
            return
        }
    }

    //From Firebase
    var serviceList: RealmList<Service>? = RealmList()
    fireGetBaseYsrObjects<Service>(FireTypes.SERVICES) {
        serviceList = (this ?: RealmList())
        serviceList?.let { Session.addServices(it) }
        rv?.loadInRealmListGrid(serviceList, context, FireTypes.SERVICES, onClick)
    }
}

class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var cardServiceImgBackground = itemView.bind<ImageView>(R.id.cardServiceImgBackground)
    private var cardServiceTxtTitle = itemView.bindTextView(R.id.cardServiceTxtTitle)
    private var cardServiceTxtCoachName = itemView.bindTextView(R.id.cardServiceTxtCoachName)
    private var cardServiceTxtTime = itemView.bindTextView(R.id.cardServiceTxtTime)
    private var cardServiceTxtLocation = itemView.bindTextView(R.id.cardServiceTxtLocation)

    fun bind(service: Service?) {
        service?.let {
            cardServiceTxtTitle?.text = it.name
            cardServiceTxtCoachName?.text = it.ownerName
            cardServiceTxtTime?.text = it.timeOfService
            cardServiceTxtLocation?.text = it.addressOne
        }
    }
}



