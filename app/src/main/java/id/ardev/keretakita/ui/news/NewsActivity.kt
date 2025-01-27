package id.ardev.keretakita.ui.news

import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import id.ardev.keretakita.R
import id.ardev.keretakita.databinding.ActivityNewsBinding

class NewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        webViewSetup()
        toolbarSetup()
    }

    private fun toolbarSetup() {
        setSupportActionBar(binding.toolbar)

        // Mengaktifkan tombol kembali
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun webViewSetup() {
        // Mengaktifkan JavaScript jika diperlukan
        val webSettings: WebSettings = binding.webviewNews.settings
        webSettings.javaScriptEnabled = true

        // Mengatur WebViewClient untuk memastikan konten dimuat di WebView
        binding.webviewNews.webViewClient = WebViewClient()

        // Memuat URL
        binding.webviewNews.loadUrl("https://keretaapikita.com")
    }

    override fun onSupportNavigateUp(): Boolean {
        // Menangani klik tombol kembali
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}