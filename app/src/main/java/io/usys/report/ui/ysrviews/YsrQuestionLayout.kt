package io.usys.report.ui.ysrviews

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.*
import io.usys.report.utils.log

class YsrQuestionLayout(context: Context?) : LinearLayout(context), AdapterView.OnItemSelectedListener {

    constructor(context: Context, attrs: AttributeSet) : this(context)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : this(context)

    var questionTextView: TextView? = null
    var choicesSpinner: Spinner? = null
    var choicesAdapter: ArrayAdapter<*>? = null
    var choiceList: ArrayList<String> = ArrayList()

    init {
        choiceList.add("What do you like about this coach?")
        choicesSpinner = Spinner(this.context)
        choicesSpinner?.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        choicesAdapter = ArrayAdapter(this.context, android.R.layout.simple_list_item_1, choiceList)
        choicesSpinner?.onItemSelectedListener = this
        choicesSpinner?.adapter = choicesAdapter
        this.addView(choicesSpinner)
    }

    /** Location Manager Spinner On Click Listener **/
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        //match the name of this location with locations in users locations
        (parent?.getChildAt(0) as? TextView)?.setTextColor(Color.WHITE)
//        finalOrganization = organizationMap[position]
//        val _id = finalOrganization?.id
//        spotAdapter?.locationId = _id ?: ""
//        spotAdapter?.notifyDataSetChanged()
        log("Location From Spinner: ")
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

}