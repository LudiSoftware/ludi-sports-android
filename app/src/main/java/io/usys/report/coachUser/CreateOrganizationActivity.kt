package io.usys.report.coachUser

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import io.usys.report.R
import io.usys.report.model.Organization
import io.usys.report.model.Session
import io.usys.report.model.Spot
import io.usys.report.model.User
import io.usys.report.utils.*
import io.realm.RealmList
import kotlinx.android.synthetic.main.activity_create_spot.*
import org.threeten.bp.LocalDate
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Created by ChazzCoin : December 2020.
 */

class CreateOrganizationActivity : AppCompatActivity() {

    val EMPTY_SPINNER = "Pick Food Type"
    val NONE = "None"

    var user : User? = null
    var listOfSpots = RealmList<Spot>()
    private var locationId : String? = null
    var organization : Organization? = null
    private var selectedDatesList : ArrayList<LocalDate> = ArrayList()
    var foodType : String? = null

    var queuedListOfSpots : RealmList<Spot> = RealmList()

    var foodtruckType: String? = null
    var foodtruckSelected: String? = null
    var foodtruckList = arrayListOf<String?>()

    private lateinit var eSpinAdapter : ArrayAdapter<String?>
    private lateinit var mSpinSpotLocation : Spinner
    private lateinit var mSpinAdapter : ArrayAdapter<String?>

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_spot)

        getProfileUpdatesFirebase()

        intent.extras?.let {
            if (it.containsKey("locationId")) { locationId = it.getString("locationId") }
            if (it.containsKey("dates")) { selectedDatesList = it.get("dates") as ArrayList<LocalDate>}
        }

        Session.session?.let { itSession ->
            user = Session.user
            itSession.organizations?.let { itLocations ->
                organization = itLocations.find { it.id == locationId }
            }
        }

//        setupSpinners()
        setupDisplay()
    }

    private fun setupDisplay() {

        //Lunch/Dinner Radio Check Boxes
        radioLunch.setOnCheckedChangeListener { _, isChecked -> toggleLunch(isChecked) }
        radioDinner.setOnCheckedChangeListener { _, isChecked -> toggleDinner(isChecked) }
        //Entree/Dessert Switches
        switchEntree.setOnCheckedChangeListener { _, _ -> verifyTwoSpots() }
        switchDessert.setOnCheckedChangeListener { _, _ -> verifyTwoSpots() }
        //ADD/CANCEL BUTTONS
        btnAdd.setOnClickListener { initAddSpotQueue() }  //-> ADD SPOTS HERE
        btnCancel.setOnClickListener { onBackPressed() }

    }

    private fun initAddSpotQueue() {
        if (!verifyInfo()) {
            //TODO: GIVE ERROR MESSAGE FOR USER
            return
        }

        foodType = getFoodTypeString()
        //Loop through each Selected Date in the list
        for (date in selectedDatesList){
            //Add spot(s) for each date
            foodType?.let { itFoodType ->
                //if two spots
                if (foodType == "both") {
                    //CREATE TWO SPOTS
                    if (!loopSpotsForSameDate(date.toString(), "both", getMealTimeString())) { return }
                    queuedListOfSpots.add(grabSpotInfo(Spot.ENTREE, date.toString()))
                    queuedListOfSpots.add(grabSpotInfo(Spot.DESSERT, date.toString()))
                } else {
                    //CREATE ONE SPOT
                    if (!loopSpotsForSameDate(date.toString(), itFoodType, getMealTimeString())) { return }
                    queuedListOfSpots.add(grabSpotInfo(itFoodType, date.toString()))
                }
            }
        }
        //Initiate the Queue
        runQueueForCreatingSpots()
    }

    private fun grabSpotInfo(_foodType:String, _date:String) : Spot {

        val newSpot = Spot()
        newSpot.id = UUID.randomUUID().toString()
        newSpot.date = _date
        newSpot.spotManager = user?.name
        newSpot.locationName = organization?.name
        newSpot.locationUUID = organization?.id
        newSpot.addressOne = organization?.addressOne
        newSpot.addressTwo = organization?.addressTwo
        newSpot.city = organization?.city
        newSpot.state = organization?.state
        newSpot.zip = organization?.zip
        newSpot.parkingInfo = organization?.details
        newSpot.foodType = _foodType
        newSpot.mealTime = getMealTimeString()
//        newSpot.estPeople = txtEstPeopleNewSpot.text.toString()
        newSpot.status = Spot.AVAILABLE
        newSpot.price = Spot.PRICE

        return newSpot
    }

    private fun loopSpotsForSameDate(newDate:String, newFoodType:String, newMealTime:String) : Boolean {
        if (listOfSpots.isNullOrEmpty()) return true
        for (spot in listOfSpots){
            val time = Spot.parseMealTime(newMealTime)
            if (spot.date == newDate && spot.mealTime == time){
                if (newFoodType == "both"){
                    //TODO: SHOW ERROR FOR USER
                    return false
                } else {
                    if (spot.foodType == newFoodType) {
                        //TODO: SHOW ERROR FOR USER
                        return false
                    }
                }
            }
        }
        return true
    }

    private fun getFoodTypeString() : String {
        if (switchEntree.isChecked && switchDessert.isChecked) return "both"
        if (switchEntree.isChecked && !switchDessert.isChecked) return Spot.ENTREE
        return Spot.DESSERT
    }

    private fun getMealTimeString() : String {
        if (radioLunch.isChecked) return Spot.LUNCH_TIME
        return Spot.DINNER_TIME
    }

    private fun verifyInfo() : Boolean {
        //At least one switch is checked.
        if (!switchEntree.isChecked && !switchDessert.isChecked) return false
//        if (txtEstPeopleNewSpot.text.isNullOrEmpty()) return false
        if (organization == null) return false
        if (organization?.addressOne.isNullOrEmpty()) return false
        if (organization?.city.isNullOrEmpty()) return false
        if (organization?.state.isNullOrEmpty()) return false
        if (organization?.zip.isNullOrEmpty()) return false
        return true
    }

    private fun verifyTwoSpots(){
        if (switchEntree.isChecked && switchDessert.isChecked){
            txtTwoSpotWarning.visibility = View.VISIBLE
        } else {
            txtTwoSpotWarning.visibility = View.GONE
        }
    }

    private fun toggleLunch(isChecked:Boolean) {
        radioLunch.isChecked = isChecked
        radioDinner.isChecked = !isChecked
    }

    private fun toggleDinner(isChecked:Boolean) {
        radioLunch.isChecked = !isChecked
        radioDinner.isChecked = isChecked
    }

    //The Spot Queue
    private fun runQueueForCreatingSpots() {
        if (queuedListOfSpots.isNullOrEmpty()) {
            //TODO: GIVE USER MESSAGE AND RETURN BACK TO CALENDAR
            onBackPressed()
            return
        }
        val spot = queuedListOfSpots.removeFirst()
        val MONTH_YEAR = spot.date?.toMonthYearForFirebase() ?: return
        addSpotToFirebase(spot, MONTH_YEAR)
    }

    //SAVE SPOT
    private fun addSpotToFirebase(spot:Spot, _MONTH_YEAR:String) {
        Log.d("SpotCalendarLocation: ", "saveSpotToFirebase")
        database = FirebaseDatabase.getInstance().reference
        database
            .child(FireHelper.AREAS).child(FireHelper.ALABAMA).child(FireHelper.BIRMINGHAM)
            .child(FireHelper.SPOTS).child(_MONTH_YEAR).child(spot.id.toString()).setValue(spot)
            .addOnSuccessListener {
                Session.createNewSpot(spot)
                runQueueForCreatingSpots()
                showSuccess(this)
            }.addOnFailureListener {
                showFailedToast(this)
            }
    }

    private fun setupSpinners() {
        eSpinAdapter = getSpinnerForFoodTruckType(this)
        val adapter = getSimpleSpinnerAdapter(this, foodtruckList)
        //Foodtruck Type
        spinFoodTruckType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val array = resources.getStringArray(R.array.foodtruck_types)
                foodtruckType = array[position]
                spinFoodTruckList.isEnabled = foodtruckType == EMPTY_SPINNER
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // sometimes you need nothing here
            }
        }
        spinFoodTruckType.adapter = eSpinAdapter
        //Sub Foodtruck Type
        spinFoodTruckList.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                //PULLED FROM LIST ON FIREBASE
                foodtruckSelected = foodtruckList[position]
                spinFoodTruckType.isEnabled = foodtruckSelected == NONE
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // sometimes you need nothing here
            }
        }
        spinFoodTruckList.adapter = adapter
    }

    private fun getProfileUpdatesFirebase() {
        database = FirebaseDatabase.getInstance().reference
        database.child(FireHelper.PROFILES).child(FireHelper.TRUCKLIST)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val temp = dataSnapshot.getValue(true) as? HashMap<*, *>
                    foodtruckList.add(NONE)
                    temp?.iterator()?.forEach {
                        foodtruckList.add(it.value.toString())
                    }
                    Log.d("this", "that")
                    setupSpinners()
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    showFailedToast(this@CreateOrganizationActivity)
                }
            })
    }
}