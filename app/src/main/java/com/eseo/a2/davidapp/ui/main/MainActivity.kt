package com.eseo.a2.davidapp.ui.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import com.eseo.a2.davidapp.R
import com.eseo.a2.davidapp.databinding.ActivityMainBinding
import com.eseo.a2.davidapp.ui.parametre.ParametreActivity
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val anims = arrayOf(R.raw.cow_animation, R.raw.salad_cat)
    private var index = 0
    private lateinit var scheduledExcecutor: ScheduledExecutorService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Home"

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