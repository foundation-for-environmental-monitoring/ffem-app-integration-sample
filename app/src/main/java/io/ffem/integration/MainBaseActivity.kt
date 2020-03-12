package io.ffem.integration

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import io.ffem.integration.PreferencesUtil.getInt
import io.ffem.integration.PreferencesUtil.setInt
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

/**
 * This class contains just the setup for the demo app only
 * This code is not required for integration.
 * Please see MainActivity for the integration code.
 */
open class MainBaseActivity : BaseActivity() {
    var selectedTest: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppTheme()
        setContentView(R.layout.activity_main)
        initialize()
    }

    fun clearResultDisplay() {
        text_result!!.text = ""
    }

    private fun initialize() {
        check_dummy_result!!.setOnCheckedChangeListener { _: CompoundButton?, _: Boolean -> clearResultDisplay() }

        val tests = ArrayList<String>()
        tests.add(Constants.AVAILABLE_IRON)
        tests.add(Constants.CALCIUM_MAGNESIUM)
        tests.add(Constants.FLUORIDE)
        tests.add(Constants.FLUORIDE_LITE)
        tests.add(Constants.INVALID_TEST)

        // Creating adapter for spinner
        val dataAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tests)

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // attaching data adapter to spinner
        spinner_list.adapter = dataAdapter
        spinner_list.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                clearResultDisplay()
                selectedTest = parent.getItemAtPosition(position).toString()

                setInt(baseContext, R.string.selected_test_key, position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // set the selected test to previously selected one
        spinner_list.setSelection(getInt(baseContext, R.string.selected_test_key, 0))
    }
}