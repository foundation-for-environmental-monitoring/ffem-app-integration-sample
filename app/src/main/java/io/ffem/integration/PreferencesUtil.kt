package io.ffem.integration

import android.content.Context
import androidx.annotation.StringRes
import androidx.preference.PreferenceManager

/**
 * Gets an integer value from preferences.
 *
 * @param context      the context
 * @param key          the key id
 * @param defaultValue the default value
 * @return stored int value
 */
fun getInt(context: Context, key: String?, defaultValue: Int): Int {
    val sharedPreferences = PreferenceManager
        .getDefaultSharedPreferences(context)
    return sharedPreferences.getInt(key, defaultValue)
}

/**
 * Functions to get/set values from/to SharedPreferences.
 */
object PreferencesUtil {

    /**
     * Gets a preference key from strings
     *
     * @param context the context
     * @param keyId   the key id
     * @return the string key
     */
    private fun getKey(context: Context, @StringRes keyId: Int): String {
        return context.getString(keyId)
    }

    /**
     * Gets a long value from preferences.
     *
     * @param context the context
     * @param keyId   the key id
     * @return the stored long value
     */
    @JvmStatic
    fun getInt(context: Context, @StringRes keyId: Int, defaultValue: Int): Int {
        return getInt(context, getKey(context, keyId), defaultValue)
    }

    @JvmStatic
    fun setInt(context: Context, @StringRes keyId: Int, value: Int) {
        setInt(context, getKey(context, keyId), value)
    }

    /**
     * Sets an integer value to preferences.
     *
     * @param context the context
     * @param key     the key id
     * @param value   the value to set
     */
    @JvmStatic
    fun setInt(context: Context, key: String?, value: Int) {
        val sharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }
}