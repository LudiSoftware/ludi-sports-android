package io.usys.report.utils

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.util.*

@SuppressLint("MissingPermission")
fun getCurrentLocation(context: Context): Location? {
    // Check if location permissions are granted
    if (ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        return null
    }
    // Get the location manager
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    // Check if GPS and network location providers are enabled
    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
        return null
    }

    // Get the last known location from the GPS or network provider
    var location: Location? = null
    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
    }
    if (location == null && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
    }

    // Return the latitude and longitude as a pair
    return location
}

fun getAddressFromLatLng(context: Context, lat: Double, lng: Double): String? {
    val geocoder = Geocoder(context, Locale.getDefault())
    var address: String? = null
    try {
        val addresses = geocoder.getFromLocation(lat, lng, 1)
        if (addresses != null && addresses.size > 0) {
            val returnedAddress = addresses[0]
            val stringBuilder = StringBuilder("")
            for (i in 0..returnedAddress.maxAddressLineIndex) {
                stringBuilder.append(returnedAddress.getAddressLine(i)).append("\n")
            }
            address = stringBuilder.toString()
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return address
}

fun filterLocationsByDistance(reference: Location, locations: List<Location>, radiusInMiles: Double): List<Location> {
    return locations.filter { location ->
        val earthRadius = 3958.8 // in miles
        val dLat = Math.toRadians(location.latitude - reference.latitude)
        val dLng = Math.toRadians(location.longitude - reference.longitude)
        val a = kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) +
                kotlin.math.cos(Math.toRadians(reference.latitude)) * kotlin.math.cos(Math.toRadians(location.latitude)) *
                kotlin.math.sin(dLng / 2) * kotlin.math.sin(dLng / 2)
        val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
        earthRadius * c <= radiusInMiles
    }
}


fun getLatLngFromCityState(context:Context, city: String, state: String): Pair<Double, Double>? {
    val geocoder = Geocoder(context, Locale.getDefault())
    val address = "$city, $state"
    val location = geocoder.getFromLocationName(address, 1)

    if (location.isNotEmpty()) {
        val latitude = location[0].latitude
        val longitude = location[0].longitude
        return Pair(latitude, longitude)
    }

    return null
}

fun getDistanceInMiles(location1: Location, location2: Location): Double {
    val earthRadius = 3958.8 // in miles

    val dLat = Math.toRadians(location2.latitude - location1.latitude)
    val dLng = Math.toRadians(location2.longitude - location1.longitude)

    val a = kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) +
            kotlin.math.cos(Math.toRadians(location1.latitude)) *
            kotlin.math.cos(Math.toRadians(location2.latitude)) *
            kotlin.math.sin(dLng / 2) *
            kotlin.math.sin(dLng / 2)

    val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))

    return earthRadius * c
}