package id.ardev.keretakita.ui.tracking

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.ardev.keretakita.R
import id.ardev.keretakita.adapter.JadwalKaAdapter
import id.ardev.keretakita.adapter.TrackingKaAdapter
import id.ardev.keretakita.databinding.ActivityTrackingKaBinding
import id.ardev.keretakita.model.data.JadwalKA
import id.ardev.keretakita.utils.FormatHelper.calculateDuration

@AndroidEntryPoint
class TrackingKaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrackingKaBinding
    private var jadwalKa: JadwalKA? = null
    private lateinit var trackingKaAdapter: TrackingKaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackingKaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            binding.toolbar.setPadding(0, systemBars.top, 0, 0)
            insets
        }

        jadwalKa = intent.getParcelableExtra("TrackingKa")

        if (jadwalKa != null) {
            displayJadwalKa(jadwalKa!!)
        } else {
            Toast.makeText(this, "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
        }

        setupTrackingStopRv()
        setupToolbar()
    }

    private fun setupTrackingStopRv() {
        trackingKaAdapter = TrackingKaAdapter(this)
        jadwalKa?.let { ka ->
            binding.rvTrackingKa.apply {
                layoutManager = LinearLayoutManager(this@TrackingKaActivity)
                adapter = trackingKaAdapter
            }
            trackingKaAdapter.differ.submitList(ka.stops)
        }
    }

    private fun displayJadwalKa(jadwalKa: JadwalKA) {
        binding.apply {
            tvNamaKa.text = "KA ${jadwalKa.name_ka} [${jadwalKa.no_ka}]"
            waktuBerangkat.text = jadwalKa.time_berangkat
            stasiunBerangkat.text = jadwalKa.stops.firstOrNull()?.station_name ?: "N/A"
            waktuTujuan.text = jadwalKa.time_tujuan
            stasiunTujuan.text = jadwalKa.stops.lastOrNull()?.station_name ?: "N/A"

            val (hours, minutes) = calculateDuration(jadwalKa.time_berangkat, jadwalKa.time_tujuan)
            val durationText = if (hours >= 24) {
                "${hours - 24} j ${minutes} m +1 hari"
            } else {
                "$hours j $minutes m"
            }
            tvDurasi.text = durationText
        }
    }

    private fun setupToolbar() {
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