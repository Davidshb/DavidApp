package com.eseo.a2.davidapp.data

import android.content.Context
import android.content.SharedPreferences
import com.eseo.a2.davidapp.data.location.MyLocation
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class LocalPreferences private constructor(context: Context) {
    private val locationSharedPreferences: SharedPreferences =
        context.getSharedPreferences("location", Context.MODE_PRIVATE)

    private val osmSharedPreferences = context.getSharedPreferences("osm", Context.MODE_PRIVATE)

    /**
     * j'ai besoin de cette variable sinon le listener ne fonctionne pas
     * de ce que j'ai compris si elle n'est pas gardé en attribut d'une class elle est supprimé en
     * mémoire donc la fonction ne peut pas être appelée
     */
    private var locationListener: SharedPreferences.OnSharedPreferenceChangeListener? = null

    /**
     * cette fonction me permet d'ajouter une localisation dans l'historique
     */
    fun addLocation(value: MyLocation) {
        val his = this.getLocationHistorique()
        his?.add(Json.encodeToString(value))
        locationSharedPreferences.edit().putStringSet(location_key, his).apply()
    }

    fun getLocationHistorique(): MutableSet<String>? {
        return locationSharedPreferences.getStringSet(location_key, mutableSetOf())
    }

    fun locationHistoriqueIsEmpty(): Boolean {
        return getLocationHistorique()!!.isEmpty()
    }

    /**
     * je bind l'action à faire lorsque l'historique change
     */
    fun registerLocationOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        locationListener = listener
        locationSharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    /**
     * j'efface mon historique
     * attention à ne pas utiliser la méthode clear qui n'est pas toujours écouté par le listenenr
     * en fonction des versions d'android
     */
    fun locationClear() {
        locationSharedPreferences.edit().remove(location_key).apply()
    }

    fun getOSM(): SharedPreferences {
        return osmSharedPreferences
    }

    companion object {
        private var INSTANCE: LocalPreferences? = null
        const val location_key = "location_history"

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
