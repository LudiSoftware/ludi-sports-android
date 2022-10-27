package io.usys.report.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import io.usys.report.R
import io.usys.report.model.*
import io.usys.report.coachUser.dashboard.LocDashViewAdapter
import io.usys.report.utils.*
import io.realm.RealmList
import io.usys.report.db.*
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.collections.HashMap


/**
 * Created by ChazzCoin : December 2019.
 */

class DashboardFragment : Fragment(), AdapterView.OnItemSelectedListener {

    lateinit var rootView : View
    //Location Manager
    private lateinit var eSpinSpotLocation : Spinner
    private lateinit var eSpinAdapter : ArrayAdapter<String?>
    private var spotAdapter : LocDashViewAdapter? = null
    private var organizationList : RealmList<Organization> = RealmList() // -> ORIGINAL LIST
    private var sportList : RealmList<Sport> = RealmList()
    private var organizationMap : HashMap<Int, Organization> = HashMap()
    private var finalOrganization : Organization? = null

    val main = CoroutineScope(Dispatchers.Main + SupervisorJob())

    lateinit var user: User

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootView = inflater.inflate(R.layout.fragment_dashboard, container, false)

        //RecyclerView Init Setup
        rootView.recyclerOrgList.layoutManager = GridLayoutManager(requireContext(), 1)
        rootView.recyclerOrgList.addItemDecoration(DividerItemDecoration(context, 0))

        userOrLogout(requireActivity()) {
            user = it
        }

        //-> Global On Click Listeners
        rootView.btnLogout.setOnClickListener {
            //FOR TESTING/ADMIN WORK ONLY
//            if (Session.isLogged){
//                createAskUserLogoutDialog(requireActivity()).show()
//            }
            val bun = bundleOf("sport" to "soccer")
            toFragment(R.id.navigation_org_list, bun)
        }

//        io { getEm2() }
//        findNavController().navigate(R.id.navigation_org_list)
//        session {
//            sportList = it.sports!!
//            rootView.recyclerViewDashboard.initRealmList(sportList, requireContext(), FireDB.SPORTS)
//        }
//        getOrganizations2()
        return rootView
    }

    private fun getEm2(): RealmList<Organization> {
        val orgListResults = getOrganizationsBlocked()
        log("just received SIZE OF ORG LIST !!!! ${orgListResults.size}")
        if (!orgListResults.isNullOrEmpty()) {
            main {
                (orgListResults as? RealmList<*>)?.let {
                    rootView.recyclerOrgList.initRealmList(
                        it, requireContext(), FireTypes.ORGANIZATIONS)
                }
            }
        }
        log("returning SIZE OF ORG LIST !!!! ${orgListResults.size}")
        return orgListResults
    }

//    private suspend fun getEm(): RealmList<Organization>? {
//        var orgListResults: RealmList<Organization>? = null
//        runBlocking {
//            orgListResults = getOrgsAsync().await()
//        }
//        return orgListResults
//    }

    private fun getOrganizations2() {
        firebase { it ->
            it.child(FireDB.ORGANIZATIONS)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (ds in dataSnapshot.children) {
                            val org: Organization? = ds.getValue(Organization::class.java)
                            org?.let {
                                Session.addOrganization(it)
                            }
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                        log("Failed")
                    }
                })
        }
    }




//    private fun getCoaches() {
//        firebase { it ->
//            it.child(FireDB.USERS).orderByChild("auth").equalTo("coach")
//                .addListenerForSingleValueEvent(object : ValueEventListener {
//                    override fun onDataChange(dataSnapshot: DataSnapshot) {
//                        val result = dataSnapshot.value as? HashMap<*, *> // <ID, <String, Any>>
//                        result?.getSafe("name")
//                        val resultList = result?.toJsonRealmList()
//                        resultList?.let { itRL ->
//                            recyclerViewDashboard.initRealmList(itRL, requireContext())
//                        }
//                        log(resultList.toString())
//                    }
//                    override fun onCancelled(databaseError: DatabaseError) {
//                        log("Failed")
//                    }
//                })
//        }
//    }

    private fun createReview() {
        val rev = Review()
        rev.apply {
            this.id = newUUID()
            this.score = 4
            this.details = "us soccer"
            this.questions = RealmList(
                Question().apply { this.question = "Are you satisfied?" },
                Question().apply { this.question = "Does this coach work well with kids?" },
                Question().apply { this.question = "Does this coach work well with parents?" },
                Question().apply { this.question = "Is this coach Chace Zanaty?" })
        }
        addUpdateDB(FireDB.REVIEWS, rev.id!!, rev)
    }

    private fun createFirstOrg() {
        val org = Organization()
        org.apply {
            this.id = newUUID()
            this.sport = "soccer"
            this.city = "birmingham"
            this.name = "JohnnysBananas"
        }
        addUpdateDB(FireDB.ORGANIZATIONS, org.id.toString(), org)
    }

    private fun createSport() {
        val sport = Sport()
        sport.apply {
            this.id = newUUID()
            this.name = "soccer"
        }
        addUpdateDB(FireDB.SPORTS, sport.id.toString(), sport)
    }

    /** Location Manager Spinner On Click Listener **/
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        //match the name of this location with locations in users locations
        (parent?.getChildAt(0) as? TextView)?.setTextColor(Color.WHITE)
        finalOrganization = organizationMap[position]
        val _id = finalOrganization?.id
        spotAdapter?.locationId = _id ?: ""
        spotAdapter?.notifyDataSetChanged()
        Log.d("Location From Spinner: ", finalOrganization.toString())
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

}