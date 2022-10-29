package io.usys.report.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import io.usys.report.R
import io.usys.report.model.*
import io.usys.report.utils.*
import io.realm.RealmList
import io.usys.report.db.*
import kotlinx.android.synthetic.main.fragment_org_list.view.*

/**
 * Created by ChazzCoin : October 2022.
 */

class CoachListFragment : YsrFragment() {

    private var hasBeenLoaded = false
    private var coachesList: RealmList<Coach>? = RealmList() // -> ORIGINAL LIST
    private var callbackFunction: ((dataSnapshot: DataSnapshot?) -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootView = inflater.inflate(R.layout.fragment_coaches_list, container, false)

        setupOnClickListeners()

        (realmObjectArg as? Organization)?.id?.let {
            getCoachesByOrg(it, callbackFunction)
        }

        return rootView
    }

    //todo: navigate to org profile and coaching list...
    override fun setupOnClickListeners() {
        callbackFunction = { ds ->
            coachesList = ds?.toRealmList()
//            rootView.recyclerList.initRealmList(coachesList, requireContext(), "")
            log(coachesList.toString())
        }
        itemOnClick = { _, obj ->
            val bun = bundleOf("sport" to (obj as? Sport)?.name)
//            toFragment(R.id.navigation_org_list, bun)
        }
    }




}
