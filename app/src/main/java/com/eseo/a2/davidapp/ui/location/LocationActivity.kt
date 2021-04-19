package com.eseo.a2.davidapp.ui.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.eseo.a2.davidapp.R
import com.eseo.a2.davidapp.data.LocalPreferences
import com.eseo.a2.davidapp.data.location.MyLocation
import com.eseo.a2.davidapp.databinding.ActivityLocationBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.serialization.ExperimentalSerializationApi
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ScaleBarOverlay
import java.util.*
import org.osmdroid.config.Configuration.*
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

@ExperimentalSerializationApi
class LocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLocationBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var eseoLocation: Location
    private lateinit var mMap: MapView
    private var markerCenter: Marker? = null
    private lateinit var eseoMarker: Marker
    private var pathDistance: Polyline? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setContentView(binding.root)

        eseoLocation = Location("")
        eseoLocation.latitude = getString(R.string.eseo_lat).toDouble()
        eseoLocation.longitude = getString(R.string.eseo_lng).toDouble()

        initMap()

        binding.locateButton.setOnClickListener {
            getLocation { location ->
                //l'enregistrer
                geocode(location)

                // je me déplace sur le nouveau point
                mMap.controller.animateTo(GeoPoint(location))

                if (markerCenter == null) {
                    markerCenter = Marker(mMap).apply {
                        title = "localisation"
                    }
                    mMap.overlays.add(markerCenter)
                }

                markerCenter!!.position = GeoPoint(location)

                if (pathDistance == null) {
                    pathDistance = Polyline(mMap).apply {
                        outlinePaint.color = Color.parseColor("#FF3B20")
                        setPoints(listOf(GeoPoint(location), GeoPoint(eseoLocation)))
                    }
                    mMap.overlays.add(pathDistance)
                }

                // la distance calculé avec Location est différente de celle utiliser par polyline
                pathDistance!!.title =
                    getString(R.string.locate_eseo_distance_text) + " " + getString(
                        R.string.location_distance_eso_text,
                        pathDistance!!.distance / 1000
                    )

                // je refresh la map
                mMap.invalidate()
                binding.locateEseoDistance.text = getString(
                    R.string.location_distance_eso_text,
                    location.distanceTo(eseoLocation) / 1000
                )
            }
        }

        supportActionBar?.apply {
            title = getString(R.string.title_localisation)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    /**
     * j'instancie ma map
     */
    private fun initMap() {
        getInstance().load(this, LocalPreferences.getInstance(this).getOSM())
        mMap = findViewById(R.id.mapview)
        mMap.isClickable = true
        mMap.setUseDataConnection(true)
        mMap.overlays.add(ScaleBarOverlay(mMap))
        mMap.setTileSource(TileSourceFactory.MAPNIK)
        mMap.controller.animateTo(GeoPoint(eseoLocation))
        eseoMarker = Marker(mMap)
        mMap.controller.zoomTo(15, 1)
        mMap.overlays.add(eseoMarker)
        eseoMarker.position = GeoPoint(eseoLocation)
        mMap.invalidate()
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(cb: (Location) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener {
                    if (it != null)
                        cb(it)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Localisation impossible", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun geocode(location: Location) {
        val r = Geocoder(this, Locale.getDefault())
        val res = r.getFromLocation(location.latitude, location.longitude, 1)

        val tmp = MyLocation(location.latitude, location.longitude, Date())
        if (res.isNotEmpty()) {
            tmp.address = res[0].getAddressLine(0)
            binding.locateText.text = tmp.address
        }

        LocalPreferences.getInstance(this).addLocation(tmp)
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

