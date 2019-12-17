package io.ffem.integration

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import io.ffem.integration.MainBaseActivity.DialogDismissListener

class ThemeSelectDialog : DialogFragment() {
    private var dismissListener: DialogDismissListener? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
        val theme = selectedTheme
        var selectedThemeIndex = 0
        val themes = resources.getStringArray(R.array.themes)
        for (i in themes.indices) {
            if (themes[i] == theme) {
                selectedThemeIndex = i
                break
            }
        }
        builder.setTitle(R.string.selected_theme)
                .setSingleChoiceItems(R.array.themes, selectedThemeIndex)
                { _: DialogInterface?, which: Int -> setPreference(which) }
        return builder.create()
    }

    private fun setPreference(which: Int) {
        val sharedPref = activity!!.getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        val themes = resources.getStringArray(R.array.themes)
        editor.putString(getString(R.string.selected_theme_key), themes[which])
        editor.apply()
        dismiss()
    }

    private val selectedTheme: String?
        get() {
            val sharedPref = activity!!.getPreferences(Context.MODE_PRIVATE)
            return sharedPref.getString(getString(R.string.selected_theme_key), "")
        }

    fun dismissListener(dismissListener: DialogDismissListener?) {
        this.dismissListener = dismissListener
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (dismissListener != null) {
            dismissListener!!.dialogDismissed(null)
        }
    }
}