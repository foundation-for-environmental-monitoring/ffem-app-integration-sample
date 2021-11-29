package io.ffem.integration

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import androidx.appcompat.app.AlertDialog
import io.ffem.integration.PreferencesUtil.getInt
import io.ffem.integration.PreferencesUtil.setInt
import io.ffem.integration.databinding.ActivityMainBinding
import java.util.*

/**
 * This class contains just the setup for the demo app only
 * This code is not required for integration.
 * Please see MainActivity for the integration code.
 */
open class MainBaseActivity : BaseActivity() {
    protected lateinit var b: ActivityMainBinding
    var selectedTest: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        val view = b.root
        setContentView(view)
        setAppTheme()
        initialize()
    }

    fun clearResultDisplay() {
        b.textResult.text = ""
    }

    private fun initialize() {
        b.checkDummyResult.setOnCheckedChangeListener { _: CompoundButton?, _: Boolean -> clearResultDisplay() }

        val tests = mutableListOf(
            AVAILABLE_IRON,
            CALCIUM_MAGNESIUM,
            WATER_CALCIUM_MAGNESIUM,
            FLUORIDE,
            FLUORIDE_LITE,
            RESIDUAL_CHLORINE_LITE,
            TOTAL_ALKALINITY,
            TOTAL_HARDNESS,
            INVALID_TEST
        )

        // Creating adapter for spinner
        val dataAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tests)

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // attaching data adapter to spinner
        b.spinnerList.adapter = dataAdapter
        b.spinnerList.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                clearResultDisplay()
                selectedTest = parent.getItemAtPosition(position).toString()

                setInt(baseContext, R.string.selected_test_key, position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // set the selected test to previously selected one
        b.spinnerList.setSelection(getInt(baseContext, R.string.selected_test_key, 0))
    }

    protected fun showAppNotInstalledDialog(appTitle: String?, externalAppAction: String?) {
        val builder = AlertDialog.Builder(this, R.style.Theme_MaterialComponents_Light_Dialog_Alert)
        builder.setTitle(R.string.app_not_found)
            .setMessage(String.format(Locale.US, getString(R.string.install_app), appTitle))
            .setPositiveButton(R.string.go_to_play_store) { _: DialogInterface?, _: Int ->
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(PLAY_STORE_URL + externalAppAction)
                    )
                )
            }
            .setNegativeButton(
                android.R.string.cancel
            ) { dialogInterface: DialogInterface, _: Int -> dialogInterface.dismiss() }
            .setCancelable(false)
            .show()
    }
}