package io.usys.report.ui.ludi.service

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmList
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.firebase.FireTypes
import io.usys.report.firebase.fireGetBaseLudiObjects
import io.usys.report.realm.model.Service
import io.usys.report.realm.model.addToSession
import io.usys.report.ui.views.listAdapters.realmList.loadInRealmListGrid
import io.usys.report.ui.views.listAdapters.realmList.loadInRealmListHorizontal
import io.usys.report.realm.sessionServices
import io.usys.report.utils.views.bind
import io.usys.report.utils.views.bindTextView

/**
 * SERVICES LIST VIEW CONTROLS
 */

fun RecyclerView?.setupServiceList(context: Context, onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)?) {
    // Load Cached Services from Session
    val rv = this
    sessionServices {
        rv?.loadInRealmListHorizontal(it, FireTypes.SERVICES, onClickReturnViewRealmObject)
        return
    }

    //Load Fresh From Firebase
    var serviceList: RealmList<Service>?
    fireGetBaseLudiObjects<Service>(FireTypes.SERVICES) {
        serviceList = (this ?: RealmList())
        serviceList.addToSession()
        rv?.loadInRealmListGrid(serviceList, FireTypes.SERVICES, onClickReturnViewRealmObject)
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
//            cardServiceTxtLocation?.text = it.address?.addressOne
        }
    }
}