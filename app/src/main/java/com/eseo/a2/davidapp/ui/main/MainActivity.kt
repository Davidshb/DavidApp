package com.eseo.a2.davidapp.ui.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.eseo.a2.davidapp.R
import com.eseo.a2.davidapp.data.LocalPreferences
import com.eseo.a2.davidapp.databinding.ActivityMainBinding
import com.eseo.a2.davidapp.ui.historique.HistoriqueActivity
import com.eseo.a2.davidapp.ui.location.LocationActivity
import com.eseo.a2.davidapp.ui.parametre.ParametreActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.serialization.ExperimentalSerializationApi
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val anims = arrayOf(R.raw.cow_animation, R.raw.salad_cat)
    private var index = 0
    private lateinit var scheduledExcecutor: ScheduledExecutorService
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val permissionRequestLocation = 99

    @ExperimentalSerializationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val pref = LocalPreferences.getInstance(this)

        pref.registerLocationOnSharedPreferenceChangeListener { _, _ ->
            runOnUiThread {
                // surement pas la bonne manière de voir si l'historique est vide mais la flemme
                // pref est gardé en mémoire ??
                val res = pref.locationHistoriqueIsEmpty()
                if (res)
                    Toast.makeText(this, "Historique supprimé !", Toast.LENGTH_LONG).show()
                binding.historique.isEnabled = !res
            }
        }


        supportActionBar?.title = "Home"

        animationSwipe()

        //action définie pour le bonton localisation
        binding.locationButton.setOnClickListener {
            if (!hasPermission())
                requestPermission {
                    startActivity(LocationActivity.getStartIntent(this))
                }
            else
                startActivity(LocationActivity.getStartIntent(this))
        }

        //différence clickable et enable ?
        //clickable ne foncitonne pas pour ce que je veux
        //binding.historique.isClickable = !pref.locationHistoriqueIsEmpty()
        //binding.historique.isEnabled = !pref.locationHistoriqueIsEmpty()

        binding.historique.isEnabled = !pref.locationHistoriqueIsEmpty()

        binding.historique.setOnClickListener {
            startActivity(HistoriqueActivity.getStartIntent(this))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            permissionRequestLocation -> {
                //lorsque que l'utilisateur a refusé
                if (!(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    MaterialDialog(this).show {
                        title(R.string.request_title)
                        message(R.string.location_ask_change_params_text)
                        positiveButton(R.string.request_location_positive_button) {
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
                        permissionRequestLocation
                    )
                    if (hasPermission())
                        cb()
                }
                negativeButton(R.string.request_location_negative_button)
            }
        } else
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                permissionRequestLocation
            )
    }

    //TODO : mettre ce truc en pause lorqu'on n'est pas sur la mainActivity avec OnPause et onResume
    /**
     * ma méthode qui swipe entre les différentes animations.
     * pour en rajouter une il faut la rajouter dans anims
     * @see anims
     */
    private fun animationSwipe() {
        scheduledExcecutor = Executors.newSingleThreadScheduledExecutor()
        scheduledExcecutor.scheduleWithFixedDelay({
            runOnUiThread {
                binding.lottieAnim.setAnimation(anims[index++])
                index %= anims.size
                //println("anim : $index")
            }
        }, 3, 3, TimeUnit.SECONDS)
    }

    /**
     * modifier mon menu personnalisé
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * je gère le clique sur les boutons dans la bar de menu
     */
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
        // j'ignore si c'est important. intuitivement je l'ai rajouté
        scheduledExcecutor.shutdown()
    }

    companion object {
        fun getStartIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}