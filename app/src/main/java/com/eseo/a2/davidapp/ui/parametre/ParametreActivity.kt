package com.eseo.a2.davidapp.ui.parametre

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.eseo.a2.davidapp.BuildConfig
import com.eseo.a2.davidapp.R
import com.eseo.a2.davidapp.databinding.ActivityParametreBinding
import com.eseo.a2.davidapp.ui.parametre.adapter.ParametreAdapter
import com.eseo.a2.davidapp.data.parametre.ParametreItem

class ParametreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityParametreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParametreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recycler.layoutManager = LinearLayoutManager(this)

        // mon tableau de paramètre
        val pl = arrayOf(
            ParametreItem(getString(R.string.params_text_application), R.drawable.settings) {
                startActivity(
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                    )
                )
            },
            ParametreItem(getString(R.string.params_text_location), R.drawable.location) {
                startActivity(
                    Intent().apply {
                        action = Settings.ACTION_LOCATION_SOURCE_SETTINGS
                    }
                )
            },
            ParametreItem(getString(R.string.params_text_maps), R.drawable.map) {
                val eseoURI =
                    Uri.parse("geo:" + getString(R.string.eseo_lat) + "," + getString(R.string.eseo_lng))
                startActivity(Intent(Intent.ACTION_VIEW, eseoURI))
            },
            ParametreItem(getString(R.string.params_text_web), R.drawable.web) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.eseo.fr")))
            },
            ParametreItem(getString(R.string.params_text_mail), R.drawable.mail) {
                val mailURI = Uri.parse("mailto:David%20Sehoubo<koffi.sehoubo@reseau.eseo.fr>")
                startActivity(Intent(Intent.ACTION_VIEW, mailURI))
            }
        )

        binding.recycler.adapter = ParametreAdapter(pl)

        supportActionBar?.apply {
            title = getString(R.string.title_parametre)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        fun getStartIntent(context: Context): Intent {
            return Intent(context, ParametreActivity::class.java)
        }
    }
}