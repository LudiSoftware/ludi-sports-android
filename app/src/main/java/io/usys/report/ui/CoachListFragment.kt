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
import io.usys.report.db.FireDB
import io.usys.report.db.FireTypes
import kotlinx.android.synthetic.main.fragment_org_list.view.*

/**
 * Created by ChazzCoin : October 2022.
 */

class CoachListFragment : YsrFragment() {

    private var hasBeenLoaded = false
    private var coachList: RealmList<Coach> = RealmList() // -> ORIGINAL LIST

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootView = inflater.inflate(R.layout.fragment_coaches_list, container, false)

        setupOnClickListeners()

        (realmObjectArg as? Organization)?.id?.let {
            getCoachesByOrg(it)
        }

        return rootView
    }

    //todo: navigate to org profile and coaching list...
    override fun setupOnClickListeners() {
        itemOnClick = { view, obj ->
            val bun = bundleOf("sport" to (obj as? Sport)?.name)
//            toFragment(R.id.navigation_org_list, bun)
        }
    }

    private fun getCoachesByOrg(orgId:String) {
        firebase { it ->
            it.child(FireDB.COACHES).orderByChild("organizationId").equalTo(orgId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        coachList.clear()
                        for (ds in dataSnapshot.children) {
                            val coach: Coach? = ds.getValue(Coach::class.java)
                            coach ?.let {
                                coachList.add(it)
                            }
                        }
                        main {
                            rootView.recyclerOrgList.initRealmList(coachList, requireContext(), FireTypes.COACHES, itemOnClick)
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                        log("Failed")
                    }
                })
        }
    }






}
