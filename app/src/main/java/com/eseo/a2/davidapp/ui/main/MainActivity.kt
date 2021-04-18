package com.eseo.a2.davidapp.ui.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.eseo.a2.davidapp.BuildConfig
import com.eseo.a2.davidapp.R
import com.eseo.a2.davidapp.databinding.ActivityMainBinding
import com.eseo.a2.davidapp.ui.location.LocationActivity
import com.eseo.a2.davidapp.ui.parametre.ParametreActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val anims = arrayOf(R.raw.cow_animation, R.raw.salad_cat)
    private var index = 0
    private lateinit var scheduledExcecutor: ScheduledExecutorService
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val PermissionRequestLocation = 99

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        supportActionBar?.title = "Home"

        animationSwipe()

        //action définie pour le bonton localisation
        binding.locationButton.setOnClickListener {
            if (!hasPermission()) {
                requestPermission {
                    startActivity(LocationActivity.getStartIntent(this))
                }
            } else
                startActivity(LocationActivity.getStartIntent(this))
        }

        binding.historique.setOnClickListener {
            //TODO
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PermissionRequestLocation -> {
                if (!(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    MaterialDialog(this).show {
                        title(R.string.request_title)
                        message(
                            null,
                            "Accéder à vos paramètres d'autorisation pour modifier les paramètres de localisation"
                        )
                        positiveButton(null, "J'y vais") {
                            startActivity(
                                Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.parse("package:$packageName")
                                )
                            )
                        }
                        negativeButton(null, "non") {
                            cancel()
                        }
                    }
                }
            }
        }
    }

    private fun hasPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission(cb: () -> Unit) {
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            MaterialDialog(this).show {
                title(R.string.request_title)
                message(R.string.request_message)
                positiveButton(R.string.request_location_positive_button) {
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        PermissionRequestLocation
                    )
                    if (hasPermission())
                        cb()
                }
                negativeButton(R.string.request_location_negative_button) {
                    //cancel()
                }
            }
        } else
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PermissionRequestLocation
            )
    }

    private fun animationSwipe() {
        scheduledExcecutor = Executors.newSingleThreadScheduledExecutor()
        scheduledExcecutor.scheduleWithFixedDelay({
            Handler(Looper.getMainLooper()).post {
                binding.lottieAnim.setAnimation(anims[index++])
                index %= anims.size
                print("anim : $index")
            }
        }, 3, 3, TimeUnit.SECONDS)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.setting_button -> {
                startActivity(ParametreActivity.getStartIntent(this))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scheduledExcecutor.shutdown()
    }

    companion object {
        fun getStartIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}