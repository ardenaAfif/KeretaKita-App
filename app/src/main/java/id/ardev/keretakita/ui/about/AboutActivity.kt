package id.ardev.keretakita.ui.about

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import id.ardev.keretakita.R
import id.ardev.keretakita.databinding.ActivityAboutBinding
import id.ardev.keretakita.ui.about.privacy.PrivacyActivity
import id.ardev.keretakita.ui.about.saweria.SaweriaActivity

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        menuListener()
        toolbarSetup()
    }

    private fun menuListener() {
        binding.apply {
            cardSaweria.setOnClickListener {
                startActivity(Intent(this@AboutActivity, SaweriaActivity::class.java))
            }
            cardPrivacy.setOnClickListener {
                startActivity(Intent(this@AboutActivity, PrivacyActivity::class.java))
            }
            cardKeluar.setOnClickListener {
                exitApplication()
            }
        }
    }

    private fun toolbarSetup() {
        setSupportActionBar(binding.toolbar)

        // Mengaktifkan tombol kembali
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun exitApplication() {
        finishAffinity()
        System.exit(0)
    }

    override fun onSupportNavigateUp(): Boolean {
        // Menangani klik tombol kembali
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}