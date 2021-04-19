package com.eseo.a2.davidapp.ui.historique

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.eseo.a2.davidapp.R
import com.eseo.a2.davidapp.data.LocalPreferences
import com.eseo.a2.davidapp.data.location.MyLocation
import com.eseo.a2.davidapp.databinding.ActivityHistoriqueBinding
import com.eseo.a2.davidapp.ui.historique.adapter.HistoriqueAdapter
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class HistoriqueActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoriqueBinding

    @ExperimentalSerializationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoriqueBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.historiqueRecycler.layoutManager = LinearLayoutManager(this)
        val his = LocalPreferences.getInstance(this).getLocationHistorique()
        val myHis: MutableSet<MyLocation> = mutableSetOf()

        his?.forEach {
            myHis.add(Json.decodeFromString(it))
        }

        // je pourrais utilise un seul tableau de Pair<MyLocation, () -> Unit> mais je voulais voir
        // si cette manière fonctionne autant
        // je passe mon tableau de MyLocation et un tableau contenant les fonctions à effectuer lors
        // du click sur les boutons
        binding.historiqueRecycler.adapter = HistoriqueAdapter(myHis.toTypedArray(), myHis.map{
            {startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("geo:" + it.latitude+ ","+ it.longitude)))}
        }.toTypedArray())

        supportActionBar?.apply {
            title = getString(R.string.title_historique)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        binding.deleteButton.setOnClickListener {
            LocalPreferences.getInstance(this).locationClear()
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        fun getStartIntent(context: Context): Intent {
            return Intent(context, HistoriqueActivity::class.java)
        }
    }
}