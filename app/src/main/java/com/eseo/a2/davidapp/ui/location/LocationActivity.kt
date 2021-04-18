package com.eseo.a2.davidapp.ui.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.eseo.a2.davidapp.R
import com.eseo.a2.davidapp.data.location.LocalPreferences
import com.eseo.a2.davidapp.databinding.ActivityLocationBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

class LocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLocationBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setContentView(binding.root)


        binding.locateButton.setOnClickListener {
            getLocation()
        }

        supportActionBar?.apply {
            title = getString(R.string.title_localisation)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener {
                    if (it != null)
                        geocode(it)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Localisation impossible", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun geocode(location: Location) {
        println(location.toString())
        val tmp = MyLocation(location.latitude, location.longitude)
        val r = Geocoder(this, Locale.getDefault())
        val res = r.getFromLocation(location.latitude, location.longitude, 1)

        if (res.isNotEmpty())
            binding.locateText.text = res[0].getAddressLine(0)
        LocalPreferences.getInstance(this).addLocation(Json.encodeToString(tmp))
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        fun getStartIntent(context: Context): Intent {
            return Intent(context, LocationActivity::class.java)
        }
    }
}

@kotlinx.serialization.Serializable
private data class MyLocation(val latitude: Double, val longitude: Double)
