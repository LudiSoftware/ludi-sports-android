package io.usys.report.ui.fragments

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.os.Bundle
import android.view.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuProvider
import io.realm.RealmList
import io.usys.report.R
import io.usys.report.databinding.FragmentDashboardBinding
import io.usys.report.firebase.*
import io.usys.report.firebase.FireTypes.Companion.USER_PROFILE_IMAGE_PATH_BY_ID
import io.usys.report.model.*
import io.usys.report.ui.loadInRealmList
import io.usys.report.utils.*


/**
 * Created by ChazzCoin : October 2022.
 */

class DashboardFragment : YsrFragment() {

    private var _binding: FragmentDashboardBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        fairRequestPermissions(arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)) { mapOfResults ->
            log(mapOfResults.toString())
        }

//        //TODO: Turn this into a PopUp Dialog -> Add to profile page menu option
//        pickImageIntent = fairGetPickImageFromGalleryIntent { itUri ->
//            log(itUri)
//            itUri.uploadToFirebaseStorage(requireContext(), USER_PROFILE_IMAGE_PATH_BY_ID(
//                getUserId() ?: return@fairGetPickImageFromGalleryIntent
//            ))
//        }
//        fairPickImageFromGalleryTest { itUri ->
//            log(itUri)
//            safeUserId { itId ->
//                itUri.uploadToFirebaseStorage(requireContext(), USER_PROFILE_IMAGE_PATH_BY_ID(itId))
//            }
//        }
//        setupMenu()
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        rootView = binding.root
        setupOnClickListeners()
        return binding.root
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.general_top_menu, menu)
//        return super.onCreateOptionsMenu(menu)
//    }

//    fun setupMenu() {
//        requireActivity().addMenuProvider(object : MenuProvider {
//            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
//                menuInflater.inflate(R.menu.general_top_menu, menu)
//            }
//
//            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
//                when (menuItem.itemId) {
//                    R.id.menu_logout -> popAskUserPickImageGallery { launchPickImageIntent() }.show()
//                    else -> {}
//                }
//                return true
//            }
//
//        })
//    }



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
//            PopFragment().show(childFragmentManager, PopFragment.TAG)
//            findNavController().navigate(R.id.action_pop)
            toFragment(R.id.navigation_org_list, bundleRealmObject(obj))
        }

    }

}

