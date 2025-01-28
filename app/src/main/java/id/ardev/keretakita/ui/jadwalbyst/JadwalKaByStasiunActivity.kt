package id.ardev.keretakita.ui.jadwalbyst

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import id.ardev.keretakita.R
import id.ardev.keretakita.databinding.ActivityJadwalKaByStasiunBinding

class JadwalKaByStasiunActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJadwalKaByStasiunBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJadwalKaByStasiunBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbarSetup()
    }

    private fun toolbarSetup() {
        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}