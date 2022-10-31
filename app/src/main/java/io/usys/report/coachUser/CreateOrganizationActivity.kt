package io.usys.report.coachUser

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.usys.report.R
import io.usys.report.model.Organization
import io.usys.report.model.User
import io.usys.report.utils.*

/**
 * Created by ChazzCoin : December 2020.
 */

class CreateOrganizationActivity : AppCompatActivity() {

    var user : User? = null
    var organization : Organization? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_spot)

        setupDisplay()
    }

    private fun setupDisplay() {
        log("setupDisplay")
    }

}