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

        val tests = mutableListOf(
                AVAILABLE_IRON,
                CALCIUM_MAGNESIUM,
                FLUORIDE,
                FLUORIDE_LITE,
                RESIDUAL_CHLORINE_LITE,
                INVALID_TEST
        )

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

    protected fun showAppNotInstalledDialog(appTitle: String?, externalAppAction: String?) {
        val builder = AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog)
        builder.setTitle(R.string.app_not_found)
                .setMessage(String.format(Locale.US, getString(R.string.install_app), appTitle))
                .setPositiveButton(R.string.go_to_play_store) { _: DialogInterface?, _: Int ->
                    startActivity(Intent(Intent.ACTION_VIEW,
                            Uri.parse(PLAY_STORE_URL + externalAppAction)))
                }
                .setNegativeButton(android.R.string.cancel
                ) { dialogInterface: DialogInterface, _: Int -> dialogInterface.dismiss() }
                .setCancelable(false)
                .show()
    }
}