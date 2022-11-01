package io.usys.report.ui.fragments

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import io.realm.RealmList
import io.usys.report.R
import io.usys.report.databinding.FragmentDashboardBinding
import io.usys.report.db.*
import io.usys.report.model.*
import io.usys.report.ui.loadInRealmList
import io.usys.report.utils.*
import java.io.*


/**
 * Created by ChazzCoin : October 2022.
 */

class DashboardFragment : YsrFragment() {

    private var _binding: FragmentDashboardBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        ysrRequestPermissions(arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)) { mapOfResults ->
            log(mapOfResults.toString())
        }

//        pickImageFromGallery {
//            log(it)
//            val f: File? = it?.let { it1 -> getFileFromUri(requireContext(), it1) }
//            val storageRef = storage.reference
//            getMasterUser()?.id?.let {
//                val path = "${FireTypes.USERS}/${it}/profile/profile_image.jpg"
//                val mountainImagesRef = storageRef.child(path)
//                val stream = FileInputStream(f)
//                val uploadTask = mountainImagesRef.putStream(stream)
//                uploadTask.addOnFailureListener {
//                    // Handle unsuccessful uploads
//                    log("Failed!")
//                }.addOnSuccessListener { taskSnapshot ->
//                    // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
//                    // ...
//                    log("Success!!")
//                }
//            }
//
//        }

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        rootView = binding.root
        setupOnClickListeners()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        setupSportsList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupSportsList() {

        session { itSession ->
            if (!itSession.sports.isNullOrEmpty()) {
                _binding?.recyclerSportList?.loadInRealmList(itSession.sports, requireContext(), FireDB.SPORTS, itemOnClick)
            } else {
                getBaseObjects<Sport>(FireTypes.SPORTS) {
                    executeRealm {
                        sportList.clear()
                        sportList = this ?: RealmList()
                        sportList.addToSession()
                    }
                    _binding?.recyclerSportList?.loadInRealmList(sportList, requireContext(), FireDB.SPORTS, itemOnClick)
                }
            }
        }

    }

    override fun setupOnClickListeners() {
        itemOnClick = { _, obj ->
            toFragment(R.id.navigation_org_list, bundleRealmObject(obj))
        }

    }

}

