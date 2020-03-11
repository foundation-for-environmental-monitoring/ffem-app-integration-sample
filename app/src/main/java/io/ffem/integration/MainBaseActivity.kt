package io.ffem.integration

import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import java.util.*

/**
 * This class contains just the setup for the demo app only
 * This code is not required for integration.
 * Please see MainActivity for the integration code.
 */
open class MainBaseActivity : BaseActivity() {
    var textResult: TextView? = null
    var dummyResultCheckBox: CheckBox? = null
    var selectedTest: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppTheme()
        setContentView(R.layout.activity_main)
        initialize()
    }

    fun clearResultDisplay() {
        textResult!!.text = ""
    }

    private fun initialize() {
        textResult = findViewById(R.id.result)
        dummyResultCheckBox = findViewById(R.id.dummyResultCheckBox)
        dummyResultCheckBox!!.setOnCheckedChangeListener { _: CompoundButton?, _: Boolean -> clearResultDisplay() }
        val spinner = findViewById<Spinner>(R.id.spinner)
        val tests = ArrayList<String>()
        tests.add(Constants.AVAILABLE_IRON)
        tests.add(Constants.CALCIUM_MAGNESIUM)
        tests.add(Constants.FLUORIDE)
        tests.add(Constants.FLUORIDE_LITE)
        tests.add(Constants.INVALID_TEST)
        // Creating adapter for spinner
        val dataAdapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, tests)
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // attaching data adapter to spinner
        spinner.adapter = dataAdapter
        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                clearResultDisplay()
                selectedTest = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
}