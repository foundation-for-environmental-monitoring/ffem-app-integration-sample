package io.ffem.integration

import android.content.Context
import android.view.Gravity
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import io.ffem.integration.R.style

open class BaseActivity : AppCompatActivity() {
//    var dismissListener = DialogDismissListener { dialog: DialogInterface? ->
//        setAppTheme()
//        finish()
//        val intent = intent
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        startActivity(intent)
//    }

    fun setAppTheme() {
        try {
            val theme = selectedTheme
            if (theme != null) {
                setTheme(getThemeResourceId(theme))
            }
        } catch (ignored: Exception) {
        }
    }

    private val selectedTheme: String?
        get() {
            val sharedPref = getPreferences(Context.MODE_PRIVATE)
            return sharedPref.getString(getString(R.string.selected_theme_key), "")
        }

    private fun getThemeResourceId(theme: String): Int {
        var resourceId = -1
        try {
            val res: Class<*> = style::class.java
            val field = res.getField("AppTheme_$theme")
            resourceId = field.getInt(null)
        } catch (ignored: Exception) {
        }
        return resourceId
    }

    fun showToastMessage(@StringRes stringKey: Int) {
        val toast = Toast.makeText(this, stringKey,
                Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.BOTTOM, 0, 250)
        toast.show()
    }
}