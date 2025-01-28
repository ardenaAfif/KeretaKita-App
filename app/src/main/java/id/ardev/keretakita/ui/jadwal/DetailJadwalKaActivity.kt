package id.ardev.keretakita.ui.jadwal

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.ardev.keretakita.adapter.JadwalKaAdapter
import id.ardev.keretakita.databinding.ActivityDetailJadwalKaBinding
import id.ardev.keretakita.model.data.JadwalKA
import id.ardev.keretakita.utils.FormatHelper
import id.ardev.keretakita.utils.FormatHelper.calculateDuration

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class DetailJadwalKaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailJadwalKaBinding
    private lateinit var jadwalKaAdapter: JadwalKaAdapter
    private var jadwalKa: JadwalKA? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailJadwalKaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        jadwalKaAdapter = JadwalKaAdapter(this)
        jadwalKa = intent.getParcelableExtra("jadwalKa")

        if (jadwalKa != null) {
            displayJadwalKa(jadwalKa!!)
        } else {
            Toast.makeText(this, "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
        }

        toolbarSetup()
        setupStopRv()
    }

    private fun toolbarSetup() {
        setSupportActionBar(binding.toolbar)

        // Mengaktifkan tombol kembali
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
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

            // Tampilkan daftar stasiun
            jadwalKaAdapter.submitList(jadwalKa.stops)
        }
    }


    private fun setupStopRv() {
        jadwalKa?.let { ka ->
            binding.rvStasiunStops.apply {
                layoutManager = LinearLayoutManager(this@DetailJadwalKaActivity)
                adapter = jadwalKaAdapter
            }
            jadwalKaAdapter.submitList(ka.stops)
        } ?: run {
            Toast.makeText(this, "Data JadwalKA tidak tersedia", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        // Menangani klik tombol kembali
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}