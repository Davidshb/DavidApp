package com.eseo.a2.davidapp.data.location

import android.content.Context
import android.content.SharedPreferences

class LocalPreferences private constructor(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("location", Context.MODE_PRIVATE)
    private val key = "location_history"

    fun addLocation(value: String) {
        val his = this.getHistorique()
        his?.add(value)
        sharedPreferences.edit().putStringSet(key, his).apply()
    }

    fun getHistorique(): MutableSet<String>? {
        return sharedPreferences.getStringSet(key, mutableSetOf())
    }

    companion object {
        private var INSTANCE: LocalPreferences? = null

        fun getInstance(context: Context): LocalPreferences {
            return INSTANCE?.let {
                INSTANCE
            } ?: run {
                INSTANCE = LocalPreferences(context)
                return INSTANCE!!
            }
        }
    }
}