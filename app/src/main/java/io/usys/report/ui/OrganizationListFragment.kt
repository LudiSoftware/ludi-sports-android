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
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking
import kotlin.collections.HashMap


/**
 * Created by ChazzCoin : December 2019.
 */

class OrganizationListFragment : Fragment() {

    lateinit var rootView: View

    //Location Manager
    private var organizationList: RealmList<Organization> = RealmList() // -> ORIGINAL LIST

    val main = CoroutineScope(Dispatchers.Main + SupervisorJob())

    lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment_org_list, container, false)


        userOrLogout(requireActivity()) {
            user = it
        }

        val sportName = getArg("sport")
        log(sportName.toString())

        return rootView
    }

}
