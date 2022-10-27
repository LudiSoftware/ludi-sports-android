package io.usys.report.coachUser

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import io.usys.report.R
import io.usys.report.model.Organization
import io.usys.report.model.Spot
import io.usys.report.model.createDetailsLocationDialog
import io.usys.report.utils.*
import io.realm.RealmList
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.calendar_day_legend.view.*
import kotlinx.android.synthetic.main.spot_calendar_day.view.*
import kotlinx.android.synthetic.main.spot_calendar_fragment.*
import kotlinx.android.synthetic.main.spot_calendar_fragment.view.*
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.format.DateTimeFormatter
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Created by ChazzCoin : December 2019.
 *
 * Select a Location from the Spinner
 * Load spots for that location on the calendar to see.
 *
 * --!!FOR LOCATION MANAGERS!!--
 *   SPOTS               grabSpotsFromFirebaseByLocation
 *  0. self.spotsToQuery = FireHelper.getSpotsForMonth(month: self.MONTH_YEAR_DB)
 *  1. self.spotsToQuery.queryOrdered(byChild:"locationName")
 *  2. if newSpot.status == FireHelper.PENDING || newSpot.status == FireHelper.BOOKED (remove)
 *
 *  LOCATIONS
 *  Make sure locations are updating correctly, even if they add one during this session
 */

class SpotCalendarFragmentLocation : Fragment(), AdapterView.OnItemSelectedListener  {

    private var spotsAdapterLocation: SpotCalendarAdapterLocation? = null

    private lateinit var database: DatabaseReference

    private var selectedDate: LocalDate? = null
    private var oldDate: LocalDate? = null
    private var selectedDatesList : ArrayList<LocalDate> = ArrayList()
    private var selectedCount = 0
    private val today = LocalDate.now()
    private lateinit var mContext : Context

    //Make a Spot
    private lateinit var mThis : SpotCalendarFragmentLocation
    private var organizationMap : HashMap<Int, Organization> = HashMap()
    private var organizationList : RealmList<Organization> = RealmList() // -> ORIGINAL LIST
    private var finalOrganization : Organization? = null
    private var finalLocationId: String? = null
    private var locationNameList : ArrayList<String?> = ArrayList() // -> USED FOR INPUT DIALOG

    private lateinit var eSpinAdapter : ArrayAdapter<String?>
    private lateinit var mSpinSpotLocation : Spinner
    private lateinit var mSpinAdapter : ArrayAdapter<String?>

    private val titleSameYearFormatter = DateTimeFormatter.ofPattern("MMMM")
    private val titleFormatter = DateTimeFormatter.ofPattern("MMM yyyy")
    private val selectionFormatter = DateTimeFormatter.ofPattern("d MMM yyyy")
    private val spots = mutableMapOf<LocalDate, List<Spot>>()
    private var masterSpotList = mutableListOf<Spot>()

    private lateinit var mRootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mRootView = inflater.inflate(R.layout.spot_calendar_fragment, container, false)
        //Get Locations
        return mRootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mContext = requireContext()
        mThis = this

        //Recycler View
        spotsAdapterLocation = SpotCalendarAdapterLocation(requireActivity())
        spotsAdapterLocation?.mContext = mContext
        spotCalendarRecyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        spotCalendarRecyclerView.adapter = spotsAdapterLocation
        spotCalendarRecyclerView.addItemDecoration(DividerItemDecoration(mContext, 0))

        val daysOfWeek = daysOfWeekFromLocale()
        //Calendar Design Setup
        setupCalendar(daysOfWeek)

        if (savedInstanceState == null) {
            navigation_spot_calendar.post {
                // Show today's events initially.
                selectDate(today)
            }
        }

        spotAddButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ft_add_icon_60dp))
        spotAddButton.setOnClickListener {
            val i = Intent(context, CreateOrganizationActivity::class.java)
            i.putExtra("locationId", finalLocationId)
            i.putExtra("dates", selectedDatesList)
            startActivity(i)
        }

    }

    private fun setupCalendar(daysOfWeek: Array<DayOfWeek>? = null) {
        val currentMonth = YearMonth.now()
        navigation_spot_calendar.setup(currentMonth.minusMonths(10),
            currentMonth.plusMonths(10), (daysOfWeek?.first() ?: daysOfWeekFromLocale().first()))
        navigation_spot_calendar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        navigation_spot_calendar.scrollToMonth(currentMonth)
        //Binders
        setupCalendarBinders()
    }

    private fun setupCalendarBinders() {

        //Day Selector
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val textView = view.spotCalendarDayText
            val dotView = view.spotCalendarDotView

            init {
                textView.setTextColor(Color.WHITE)
                view.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH) {
                        //User has selected date here!
                        selectDate(day.date)
                    }
                }
            }
        }

        navigation_spot_calendar.dayBinder = object : DayBinder<DayViewContainer> {
            lateinit var dotView : View

            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                val textView = container.textView
                dotView = container.dotView

                textView.text = day.date.dayOfMonth.toString()

                if (day.owner == DayOwner.THIS_MONTH) {
                    textView.makeVisible()
                    when (day.date) {
                        today -> {
                            textView.setTextColorRes(R.color.example_3_white)
                            textView.setBackgroundResource(R.drawable.spot_today_bg)
                            dotView.makeInVisible()
                        }
                        selectedDate -> {
                            if (oldDate != null && oldDate == selectedDate && selectedCount < 2) {
                                //-> UnSelect
                                textView.setTextColorRes(R.color.example_3_white)
                                textView.background = null
                                dotView.isVisible = spots[day.date].orEmpty().isNotEmpty()
                                oldDate = null
                            } else {
                                //-> Select
                                textView.setTextColorRes(R.color.example_3_blue)
                                textView.setBackgroundResource(R.drawable.spot_selected_bg)
                                dotView.makeInVisible()
                                oldDate = null
                            }
                        }
                        else -> {
                            textView.setTextColorRes(R.color.example_3_white)
                            textView.background = null
                            dotView.isVisible = spots[day.date].orEmpty().isNotEmpty()
                        }
                    }
                } else {
                    textView.makeInVisible()
                    dotView.makeInVisible()
                }
            }
        }

        //Scroll Listener
        navigation_spot_calendar.monthScrollListener = {
            (activity as AppCompatActivity).supportActionBar?.title = if (it.year == today.year) {
                titleSameYearFormatter.format(it.yearMonth)
            } else {
                titleFormatter.format(it.yearMonth)
            }
            // Select the first day of the month when
            // we scroll to a new month.
            selectedCount = 0
            selectedDatesList.clear()
            spotsAdapterLocation?.spots?.clear()
            //get new month worth of spots
            masterSpotList.clear()
            val newMonth = it.yearMonth.atDay(1).toMonthYearForFirebase() ?: ""
            val locName = finalOrganization?.name ?: ""
            getSpotsFromFirebaseByLocation(newMonth, locName)
            selectDate(it.yearMonth.atDay(1))
        }

        //Month Container
        class MonthViewContainer(view: View) : ViewContainer(view) {
            val legendLayout = view.legendLayout
        }

        //Month Binder
        navigation_spot_calendar.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                // Setup each header day text if we have not done that already.
                if (container.legendLayout.tag == null) {
                    container.legendLayout.tag = month.yearMonth
                    container.legendLayout.children.map { it as TextView }.forEachIndexed { index, tv ->
                        tv.text = daysOfWeekFromLocale()[index].name.first().toString()
                        tv.setTextColorRes(R.color.example_3_black)
                    }
                }
            }
        }

    }

    fun setupSpinners() {
        //Spinner Setup
        locations {
            organizationList = it
            prepareListOfLocations()
            mSpinAdapter = getSimpleSpinnerAdapter(requireContext(), locationNameList)
            eSpinAdapter = getSimpleSpinnerAdapter(requireContext(), locationNameList)
        }
        mSpinSpotLocation = mRootView.spinLocCalendar
        mSpinSpotLocation.onItemSelectedListener = this
        mSpinSpotLocation.adapter = mSpinAdapter
        mSpinAdapter.notifyDataSetChanged()
    }


    private fun prepareListOfLocations() {
        locationNameList.clear()
        for ((i, location) in organizationList.withIndex()){
            locationNameList.add(location.name)
            organizationMap[i] = location
        }
    }

    private fun selectDate(date: LocalDate) {
        oldDate = selectedDate
        selectedDate = date

        if (selectedCount < 2) {
            selectedCount += 1
            return
        }

        //if date already exists in the list, remove it
        if (selectedDatesList.contains(date)) {
            selectedDatesList.remove(date)
            selectedDate = null
            //If Master Spot List doesn't have a spot for the selected date
            //Update the calendar and move on
            if (!hasSpotForDate(date)) {
                navigation_spot_calendar.notifyDateChanged(date)
                return
            }

            //Master Spot List DOES have spot(s) for the date
            //remove that spot
            updateSelectedDateFromAdapter()

            //Update UI
            navigation_spot_calendar.notifyDateChanged(date)
            spotsAdapterLocation?.notifyDataSetChanged()

        } else {
            //else add it
            selectedDatesList.add(date)
            updateSelectedDateFromAdapter()
            spotsAdapterLocation?.notifyDataSetChanged()
            //Update UI
            navigation_spot_calendar.notifyDateChanged(date)
            spotCalendarSelectedDateText.text = selectionFormatter.format(date)
        }

    }

    private fun updateSelectedDateFromAdapter() {
        //-> Loop through ALL Spots
        spotsAdapterLocation?.spots?.clear()
        masterSpotList.iterator().forEach { itSpot ->
            val masterSpotDate = convertStringToDate(itSpot.date!!)
            selectedDatesList.iterator().forEach {
                if (masterSpotDate == it) {
                    //If Add
                    if (!spotsAdapterLocation?.spots?.contains(itSpot)!!) {
                        spotsAdapterLocation?.spots?.add(itSpot)
                    }
                }
            }
        }
    }

    private fun hasSpotForDate(date: LocalDate) : Boolean {
        masterSpotList.iterator().forEach { itSpot ->
            val itSpotDate = convertStringToDate(itSpot.date!!)
            if (itSpotDate == date) {
                return true
            }
        }
        return false
    }

    private fun saveSpotAtDateTime(spot: Spot) {
        spot.date?.let {
            val temp = convertStringToDate(it)
            spots[temp] = spots[temp].orEmpty().plus(spot)
        }
    }

    private fun clearAdapter() {
        spotsAdapterLocation?.removeAllSpots()
        spotsAdapterLocation?.notifyDataSetChanged()
        spots.clear()
        navigation_spot_calendar.refreshDrawableState()
        navigation_spot_calendar.notifyCalendarChanged()
    }

    //REMOVE SPOT
    private fun removeSpotFromFirebase(spot : Spot, pos: Int) {
        Log.d("SpotCalendarLocation: ", "removeSpotToFirebase")
        database = FirebaseDatabase.getInstance().reference
        database
            .child(FireHelper.AREAS).child(FireHelper.ALABAMA).child(FireHelper.BIRMINGHAM)
            .child(FireHelper.SPOTS).child(spot.id.toString()).removeValue()
            .addOnSuccessListener {
                //TODO("HANDLE SUCCESS")
                this.selectedDate?.let {
//                    updateAdapter(it)
                }
                showSuccess(requireContext())
            }.addOnCompleteListener {
                //TODO("HANDLE COMPLETE")
            }.addOnFailureListener {
                //TODO("HANDLE FAILURE")
                showFailedToast(requireContext())
            }
    }

    private fun getSpotsFromFirebaseByLocation(monthYear:String, locationName:String) {
        Log.d("SpotCalendarLocation: ", "getSpotsFromFirebase")
        database = FirebaseDatabase.getInstance().reference
        database.child(FireHelper.AREAS).child(FireHelper.ALABAMA)
            .child(FireHelper.BIRMINGHAM).child(FireHelper.SPOTS).child(monthYear)
            .orderByChild("locationName").equalTo(locationName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val spot: Spot? = ds.getValue(Spot::class.java)
                    spot?.let {
                        saveSpotAtDateTime(it)
                        masterSpotList.add(it)
                    }
                }
                //DO SETUP FOR CALENDAR
                navigation_spot_calendar.refreshDrawableState()
                navigation_spot_calendar.notifyCalendarChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                //TODO: ADD CRASHALYTICS HERE
                showFailedToast(requireContext())
            }
        })
    }


    override fun onStart() {
        super.onStart()
        requireActivity().window.statusBarColor = requireContext().getColorCompat(R.color.example_3_statusbar_color)
    }

    override fun onStop() {
        super.onStop()
        requireActivity().window.statusBarColor = requireContext().getColorCompat(R.color.colorPrimaryDark)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        //
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        //match the name of this location with locations in users locations
        finalOrganization = organizationMap[position]
        finalLocationId = finalOrganization?.id
        clearAdapter()
        getSpotsFromFirebaseByLocation(getMonthYearForFirebase(), finalOrganization?.name ?: "")
        Log.d("Location From Spinner: ", finalOrganization.toString())
    }
}


/** ADAPTER AND VIEWHOLDER **/
class SpotCalendarAdapterLocation(var activity: Activity) : //val onClick: (Spot) -> Unit
    RecyclerView.Adapter<SpotCalendarAdapterLocation.SpotsViewHolderLocation>() {

    var mContext: Context? = null
    var spots = mutableListOf<Spot>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpotsViewHolderLocation {
        return SpotsViewHolderLocation(parent.inflate(R.layout.item_list_sports_two), activity)
    }

    override fun onBindViewHolder(viewHolder: SpotsViewHolderLocation, position: Int) {
        viewHolder.bind(spots[position])
    }

    override fun getItemCount(): Int = spots.size

    fun removeAllSpots() {
        spots.clear()
        spots = mutableListOf()
    }

    inner class SpotsViewHolderLocation(override val containerView: View, val activity: Activity) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(spot: Spot) {
//            containerView.txtItemSpotName.text = spot.locationName
//            containerView.txtItemDate.text = spot.toFullDate()
//            containerView.txtItemEstPeople.text = spot.estPeople
//            containerView.txtItemCost.text = spot.toFullPrice()

            containerView.setOnClickListener {
                spot.createDetailsLocationDialog(activity = activity).show()
            }
        }
    }
}