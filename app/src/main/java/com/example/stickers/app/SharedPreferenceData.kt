package com.example.stickers.app


import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceData(private val activity: Context) {
    fun putString(key: String, value: String) {
        val sharedPref: SharedPreferences =
            activity.getSharedPreferences("preferenceName", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(key, value)
        editor.apply()
    }
    fun getString(key: String, default: String? = null): String? {
        val sharedPref: SharedPreferences =
            activity.getSharedPreferences("preferenceName", Context.MODE_PRIVATE)
        return sharedPref.getString(key, default)
    }
    fun putBoolean(key: String, value: Boolean) {
        val sharedPref: SharedPreferences =
            activity.getSharedPreferences("preferenceName", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBoolean(key: String, default: Boolean = false): Boolean {
        val sharedPref: SharedPreferences =
            activity.getSharedPreferences("preferenceName", Context.MODE_PRIVATE)
        return sharedPref.getBoolean(key, default)
    }
}
