package io.usys.report.utils

/**
 * Created by ChazzCoin : December 2019.
 */

class FireHelper {

    companion object {
        const val SPOT_MONTH_DB = "MMMyyyy"
        const val SPOT_DATE_FORMAT = "yyyy-MM-d"
        const val DATE_MONTH = "MMMM"

        const val AVAILABLE: String = "available"
        const val PENDING: String = "pending"
        const val BOOKED: String = "booked"
        const val WAITING: String = "waiting"

        const val paymentIntentUrl = "https://us-central1-food-truck-finder-91dc0.cloudfunctions.net/charge/"
        const val FIRE_DATE_FORMAT = "EEE, MMM d yyyy, hh:mm:ss a"
        //ADMIN
        const val ADMIN: String = "admin"
        const val SYSTEM: String = "System"
        const val TRUCKLIST: String = "TruckList"  //"TruckList"
        const val FOODTRUCK_MANAGER: String = "foodtruck_manager"
        const val LOCATION_MANAGER: String = "location_manager"
        const val PROFILES: String = "Profiles"
        const val USERS: String = "users"
        const val LOCATIONS: String = "locations"
        const val FOODTRUCKS: String = "foodtrucks"
        //City
        const val AREAS: String = "areas"
        const val ALABAMA: String = "alabama"
        const val BIRMINGHAM: String = "birmingham"
//        const val MONTH: String? = null
        const val SPOTS: String = "spots"

        //Reviews
        const val REVIEWS: String = "Reviews" //Reviews -> UserUUID -> ReviewUUID -> Review Obj

    }

    /** Essentially, have the Spots, Locations, etc from Firebase
     * loaded in the background into the "session" accordingly **/

}