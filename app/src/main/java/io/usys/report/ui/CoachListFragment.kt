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
import io.usys.report.model.Coach.Companion.ORDER_BY_ORGANIZATION
import kotlinx.android.synthetic.main.fragment_org_list.view.*

/**
 * Created by ChazzCoin : October 2022.
 */

class CoachListFragment : YsrFragment() {

    private var coachesList: RealmList<Coach>? = RealmList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootView = inflater.inflate(R.layout.fragment_coaches_list, container, false)

        setupOnClickListeners()

        (realmObjectArg as? Organization)?.id?.let {
            getOrderByEqualTo(FireDB.COACHES, ORDER_BY_ORGANIZATION, it) {
                coachesList = this?.toRealmList()
                rootView.recyclerList.loadInRealmList(coachesList, requireContext(), FireDB.COACHES, itemOnClick)
            }
        }

        return rootView
    }

    //todo: navigate to org profile and coaching list...
    override fun setupOnClickListeners() {
        itemOnClick = { _, obj ->
            toFragment(R.id.navigation_profile, obj)
        }
    }




}
