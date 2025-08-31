package id.ardev.keretakita.ui.news

import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import id.ardev.keretakita.R
import id.ardev.keretakita.databinding.ActivityNewsBinding

class NewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            binding.toolbar.setPadding(0, systemBars.top, 0, 0)
            insets
        }

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