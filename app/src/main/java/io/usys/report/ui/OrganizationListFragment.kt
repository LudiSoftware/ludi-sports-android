package io.usys.report.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Created by ChazzCoin : October 2022.
 */

class OrganizationListFragment : Fragment() {

    lateinit var rootView: View
    private var organizationList: RealmList<Organization> = RealmList() // -> ORIGINAL LIST
    val main = CoroutineScope(Dispatchers.Main + SupervisorJob())
    lateinit var user: User

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootView = inflater.inflate(R.layout.fragment_org_list, container, false)

        userOrLogout(requireActivity()) {
            user = it
        }

        val sportName = getArg("sport")
        log(sportName.toString())
        getOrganizationsBySport(sportName.toString())
        return rootView
    }

    private fun getOrganizationsBySport(sport:String) {
        firebase { it ->
            it.child(FireDB.ORGANIZATIONS).orderByChild("sport").equalTo(sport)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (ds in dataSnapshot.children) {
                            val org: Organization? = ds.getValue(Organization::class.java)
                            org?.let {
                                organizationList.add(it)
                            }
                        }
                        main {
                            rootView.recyclerOrgList.initRealmList(organizationList, requireContext(), FireTypes.ORGANIZATIONS)
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                        log("Failed")
                    }
                })
        }
    }


    //todo: navigate to org profile and coaching list...



}
