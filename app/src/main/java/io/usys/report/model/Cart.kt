package io.usys.report.model

import android.content.Context
import android.widget.Toast
import androidx.room.PrimaryKey
import io.usys.report.utils.executeRealm
import io.usys.report.utils.realm
import io.realm.RealmList
import io.realm.RealmObject

/**
 * Created by ChazzCoin : December 2019.
 */
open class Cart : RealmObject() {

    companion object {
        var TOTAL_COST : Double = 0.0
    }

    @PrimaryKey
    var id : String = "111"
    var spots: RealmList<Spot> = RealmList()
    var totalCost: Double = 0.0

    fun addSpot(spot:Spot){
        this.spots.add(spot)
        addTotalCost(spot)
    }

    private fun addTotalCost(spot: Spot) {
        spot.price?.let {
            val spotPrice = it.toDouble()
            val temp = this.totalCost + spotPrice
            this.totalCost = temp
        }
        TOTAL_COST = this.totalCost
    }

    fun removeSpot(spot:Spot){
        this.spots.remove(spot)
        subtractTotalCost(spot)
    }

    private fun subtractTotalCost(spot: Spot) {
        spot.price?.let {
            val spotPrice = it.toDouble()
            val temp = this.totalCost - spotPrice
            this.totalCost = temp
        }
        TOTAL_COST = this.totalCost
    }

    fun toFullPrice() : String {
        return "$${this.totalCost}0"
    }

    fun toPriceForStripe() : String {
        return this.totalCost.toString().replace(".", "0")
    }

}

fun Cart?.isNullorEmpty() : Boolean {
    this?.let {
        if (it.spots.isNullOrEmpty()) return true
        return false
    } ?: kotlin.run {
        return true
    }
}

/** -> CART <- **/
//CREATE NEW SESSION
fun createCart() {
    val cart = Cart()
    executeRealm { itRealm ->
        itRealm.createObject(Cart::class.java)
        itRealm.insert(cart)
    }

}

//GET CURRENT CART
fun getCart(): Cart? {
    return realm().where(Cart::class.java).equalTo("id", "111").findFirst()
}

//UPDATE CURRENT SESSION
fun addSpotToCart(spot: Spot, mContext : Context) {
    val cart = getCart()
    executeRealm { itRealm ->
        cart?.addSpot(spot)
        if (cart != null) { itRealm.insertOrUpdate(cart) }
        Toast.makeText(mContext, "Successfully added Spot to Cart!", Toast.LENGTH_LONG).show()
    }
}

fun removeSpotFromCart(spot: Spot, mContext : Context) {
    val cart = getCart()
    executeRealm { itRealm ->
        cart?.removeSpot(spot)
        if (cart != null) { itRealm.insertOrUpdate(cart) }
        Toast.makeText(mContext, "Successfully removed Spot from Cart!", Toast.LENGTH_LONG).show()
    }
}
