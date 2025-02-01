package id.ardev.keretakita.ui.about.saweria

import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import id.ardev.keretakita.R
import id.ardev.keretakita.databinding.ActivitySaweriaBinding

class SaweriaActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySaweriaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySaweriaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbarSetup()
        webViewSetup()
    }

    private fun webViewSetup() {
        // Mengaktifkan JavaScript jika diperlukan
        val webSettings: WebSettings = binding.webviewSaweria.settings
        webSettings.javaScriptEnabled = true

        // Mengatur WebViewClient untuk memastikan konten dimuat di WebView
        binding.webviewSaweria.webViewClient = WebViewClient()

        // Memuat URL
        binding.webviewSaweria.loadUrl("https://saweria.co/ardevCreations")
    }

    private fun toolbarSetup() {
        setSupportActionBar(binding.toolbar)

        // Mengaktifkan tombol kembali
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        // Menangani klik tombol kembali
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}