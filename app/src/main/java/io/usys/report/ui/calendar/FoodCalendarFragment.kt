package io.usys.report.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.database.*
import io.usys.report.R
import io.usys.report.model.Spot
import io.usys.report.utils.*
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.format.DateTimeFormatter

class FoodCalendarFragment : Fragment() {
    private lateinit var database: DatabaseReference

    private var selectedDate: LocalDate? = null
    private val today = LocalDate.now()

    private val titleSameYearFormatter = DateTimeFormatter.ofPattern("MMMM")
    private val titleFormatter = DateTimeFormatter.ofPattern("MMM yyyy")
    private val selectionFormatter = DateTimeFormatter.ofPattern("d MMM yyyy")
    private val spots = mutableMapOf<LocalDate, List<Spot>>()

    var spotsListAdapter : FoodCalendarViewAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.spot_calendar_fragment, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val daysOfWeek = daysOfWeekFromLocale()
        val currentMonth = YearMonth.now()


        //Day Selector
//        class DayViewContainer(view: View) : ViewContainer(view) {
//            lateinit var day: CalendarDay // Will be set when this container is bound.
//            val textView = view.spotCalendarDayText
//            val dotView = view.spotCalendarDotView
//
//            init {
//                view.setOnClickListener {
//                    if (day.owner == DayOwner.THIS_MONTH) {
//                        selectDate(day.date)
//                    }
//                }
//            }
//        }

        //Day Binder
//        navigation_spot_calendar.dayBinder = object : DayBinder<DayViewContainer> {
//            override fun create(view: View) = DayViewContainer(view)
//            override fun bind(container: DayViewContainer, day: CalendarDay) {
//                container.day = day
//                val textView = container.textView
//                val dotView = container.dotView
//                dotView.setBackgroundColor(Color.RED)
//
//                textView.text = day.date.dayOfMonth.toString()
//
//                if (day.owner == DayOwner.THIS_MONTH) {
//                    textView.makeVisible()
//                    when (day.date) {
//                        today -> {
//                            textView.setTextColorRes(R.color.example_3_white)
//                            textView.setBackgroundResource(R.drawable.spot_today_bg)
//                            dotView.makeInVisible()
//                        }
//                        selectedDate -> {
//                            textView.setTextColorRes(R.color.example_3_black)
//                            textView.setBackgroundResource(R.drawable.spot_selected_bg)
//                            dotView.makeInVisible()
//                        }
//                        else -> {
//                            textView.setTextColor(Color.WHITE)
//                            textView.background = null
//                            dotView.isVisible = spots[day.date].orEmpty().isNotEmpty()
//                        }
//                    }
//                } else {
//                    textView.makeInVisible()
//                    dotView.makeInVisible()
//                }
//            }
//        }

//        //Scroll Listener
//        navigation_spot_calendar.monthScrollListener = {
//            (activity as AppCompatActivity).supportActionBar?.title = if (it.year == today.year) {
//                titleSameYearFormatter.format(it.yearMonth)
//            } else {
//                titleFormatter.format(it.yearMonth)
//            }
//            // Select the first day of the month when
//            // we scroll to a new month.
//            selectDate(it.yearMonth.atDay(1))
//        }

//        //Month Container
//        class MonthViewContainer(view: View) : ViewContainer(view) {
//            val legendLayout = view.legendLayout
//        }

//        //Month Binder
//        navigation_spot_calendar.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
//            override fun create(view: View) = MonthViewContainer(view)
//            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
//                // Setup each header day text if we have not done that already.
//                if (container.legendLayout.tag == null) {
//                    container.legendLayout.tag = month.yearMonth
//                    container.legendLayout.children.map { it as TextView }.forEachIndexed { index, tv ->
//                        tv.text = daysOfWeek[index].name.first().toString()
//                        tv.setTextColorRes(R.color.example_3_black)
//                    }
//                }
//            }
//        }
//        spotAddButton.hide()
//    }

//    private fun selectDate(date: LocalDate) {
//        if (selectedDate != date) {
//            val oldDate = selectedDate
//            selectedDate = date
//            spotsListAdapter?.currentDate = selectedDate?.toString()
//            spotsListAdapter?.notifyDataSetChanged()
//            oldDate?.let { navigation_spot_calendar.notifyDateChanged(it) }
//            navigation_spot_calendar.notifyDateChanged(date)
//            updateAdapterForDate(date)
//        }
//    }

//    private fun saveSpotAtDateTime(spot: Spot) {
//        spot.date?.let {
//            val date = LocalDate.parse(it)
//            spots[date] = spots[date].orEmpty().plus(spot)
//            updateAdapterForDate(date)
//            spotsListAdapter?.arrayOfSpots?.add(spot)
//            spotsListAdapter?.notifyDataSetChanged()
//        }
//    }

//    private fun updateAdapterForDate(date: LocalDate) {
//        spotsListAdapter?.arrayOfSpots?.clear()
//        spotsListAdapter?.arrayOfSpots?.addAll(spots[date].orEmpty())
//        spotsListAdapter?.notifyDataSetChanged()
//        spotCalendarSelectedDateText.text = selectionFormatter.format(date)
//    }


//    override fun onStart() {
//        super.onStart()
//        requireActivity().window.statusBarColor = requireContext().getColorCompat(R.color.example_3_statusbar_color)
//    }

//    override fun onStop() {
//        super.onStop()
//        requireActivity().window.statusBarColor = requireContext().getColorCompat(R.color.colorPrimaryDark)
//    }
    }
}