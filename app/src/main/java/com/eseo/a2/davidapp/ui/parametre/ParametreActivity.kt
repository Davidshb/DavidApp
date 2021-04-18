package com.eseo.a2.davidapp.ui.parametre

import android.Manifest
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
import com.eseo.a2.davidapp.ui.parametre.adapter.ParametreItem

class ParametreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityParametreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParametreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recycler.layoutManager = LinearLayoutManager(this)

        val pl = arrayOf(
            ParametreItem("paramètre application", R.drawable.settings) {
                startActivity(
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                    )
                )
            },
            ParametreItem("localisation", R.drawable.location) {
                startActivity(
                    Intent().apply {
                        action = Settings.ACTION_LOCATION_SOURCE_SETTINGS
                    }
                )
            },
            ParametreItem("ouvrir carte", R.drawable.map) {
                val eseoURI = Uri.parse("geo:47.493127917011186,-0.5513476199672469")
                startActivity(Intent(Intent.ACTION_VIEW, eseoURI))
            },
            ParametreItem("site web", R.drawable.web) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.eseo.fr")))
            },
            ParametreItem("envoyer un mail", R.drawable.mail) {
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