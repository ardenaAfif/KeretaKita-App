package id.ardev.keretakita.ui.stasiun

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dagger.hilt.android.AndroidEntryPoint
import id.ardev.keretakita.databinding.ActivityDetailInfoStasiunBinding
import id.ardev.keretakita.model.data.Stasiun
import java.net.URLEncoder

@AndroidEntryPoint
class DetailInfoStasiunActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailInfoStasiunBinding
    private var stasiun: Stasiun? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailInfoStasiunBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            binding.toolbar.setPadding(0, systemBars.top, 0, 0)
            insets
        }

        stasiun = intent.getParcelableExtra("EXTRA_STASIUN")

        toolbarSetup()
        pdfViewerSetup()
    }

    private fun pdfViewerSetup() {
        stasiun?.let {
            if(it.infoStasiunUrl.isNotBlank()) {
                // PDF Render
                val webView = binding.webView
                webView.settings.javaScriptEnabled = true
                val encodedUrl = URLEncoder.encode(it.infoStasiunUrl, "UTF-8")
                webView.loadUrl("https://docs.google.com/gview?embedded=true&url=$encodedUrl")
                Log.d(">>PDF_URL", "URL: ${stasiun?.infoStasiunUrl}")
            } else {
                showError("URL PDF tidak tersedia")
            }
        } ?: showError("Data stasiun tidak valid")
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun toolbarSetup() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = stasiun?.nameStasiun ?: "Info Stasiun"
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}