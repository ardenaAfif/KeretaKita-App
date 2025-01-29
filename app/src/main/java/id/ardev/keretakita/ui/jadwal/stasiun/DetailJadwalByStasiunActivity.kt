package id.ardev.keretakita.ui.jadwal.stasiun

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.ardev.keretakita.R
import id.ardev.keretakita.adapter.JadwalKaAdapter
import id.ardev.keretakita.databinding.ActivityDetailJadwalByStasiunBinding
import id.ardev.keretakita.model.data.JadwalKA

@AndroidEntryPoint
class DetailJadwalByStasiunActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailJadwalByStasiunBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailJadwalByStasiunBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbarAction()
        jadwalKaSetup()
        rvJadwalByStasiun()
    }

    private fun toolbarAction() {
        setSupportActionBar(binding.toolbar)

        // Mengaktifkan tombol kembali
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun rvJadwalByStasiun() {
        val jadwalKa: JadwalKA? = intent.getParcelableExtra("EXTRA_JADWAL_KA")
        val stasiunBerangkat = intent.getStringExtra("EXTRA_STASIUN_BERANGKAT") ?: ""
        val stasiunTujuan = intent.getStringExtra("EXTRA_STASIUN_TUJUAN") ?: ""

        jadwalKa?.let { jadwal ->
            val stops = jadwal.stops

            // Cari indeks stasiun berangkat dan tujuan
            val startIndex = stops.indexOfFirst { it.station_name == stasiunBerangkat }
            val endIndex = stops.indexOfFirst { it.station_name == stasiunTujuan }

            if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
                val filteredStops = stops.subList(startIndex, endIndex + 1)

                // Set data ke adapter
                val adapter = JadwalKaAdapter(this)
                binding.rvStasiunStops.adapter = adapter
                binding.rvStasiunStops.layoutManager = LinearLayoutManager(this)
                adapter.submitList(filteredStops)
            }
        }
    }

    private fun jadwalKaSetup() {
        val namaKa = intent.getStringExtra("EXTRA_NAMA_KA") ?: "-"
        val noKa = intent.getStringExtra("EXTRA_NO_KA") ?: "-"
        val stasiunBerangkat = intent.getStringExtra("EXTRA_STASIUN_BERANGKAT") ?: "-"
        val waktuBerangkat = intent.getStringExtra("EXTRA_WAKTU_BERANGKAT") ?: "-"
        val stasiunTujuan = intent.getStringExtra("EXTRA_STASIUN_TUJUAN") ?: "-"
        val waktuTujuan = intent.getStringExtra("EXTRA_WAKTU_TUJUAN") ?: "-"
        val durasi = intent.getStringExtra("EXTRA_DURASI") ?: "-"

        // Set ke UI
        binding.tvNamaKa.text = "$namaKa [$noKa]"
        binding.stasiunBerangkat.text = stasiunBerangkat
        binding.waktuBerangkat.text = waktuBerangkat
        binding.stasiunTujuan.text = stasiunTujuan
        binding.waktuTujuan.text = waktuTujuan
        binding.tvDurasi.text = durasi
    }

    override fun onSupportNavigateUp(): Boolean {
        // Menangani klik tombol kembali
        onBackPressedDispatcher.onBackPressed()
        return true
    }

}