package id.ardev.keretakita.ui.jadwal.stasiun

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.ardev.keretakita.adapter.JadwalKaByStasiunAdapter
import id.ardev.keretakita.databinding.ActivityJadwalKaByStasiunBinding
import id.ardev.keretakita.model.data.Stasiun
import id.ardev.keretakita.model.firebase.FirebaseClient
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class JadwalKaByStasiunActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJadwalKaByStasiunBinding
    private lateinit var adapter: JadwalKaByStasiunAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJadwalKaByStasiunBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getStasiuName()
        rvKaByStasiun()
        toolbarSetup()
    }

    private fun rvKaByStasiun() {
        val stasiunBerangkat = intent.getParcelableExtra<Stasiun>("STASIUN_BRNGKT")
        val stasiunTujuan = intent.getParcelableExtra<Stasiun>("STASIUN_TUJUAN")

        if (stasiunBerangkat != null && stasiunTujuan != null) {
            showLoading()

            lifecycleScope.launch {
                try {
                    val jadwalList = FirebaseClient().getKaByStasiun(
                        stasiunBerangkat.nameStasiun,
                        stasiunTujuan.nameStasiun
                    )

                    if (jadwalList.isNotEmpty()) {
                        val sortedJadwalList = jadwalList.sortedBy { jadwal ->
                            val stopBerangkat = jadwal.stops.firstOrNull { it.station_name == stasiunBerangkat.nameStasiun }
                            stopBerangkat?.departure_time?.let { parseTime(it) } ?: LocalTime.MAX
                        }

                        adapter = JadwalKaByStasiunAdapter(
                            this@JadwalKaByStasiunActivity,
                            sortedJadwalList,
                            stasiunBerangkat.nameStasiun,
                            stasiunTujuan.nameStasiun
                        )
                        binding.rvKeretaList.apply {
                            layoutManager = LinearLayoutManager(this@JadwalKaByStasiunActivity)
                            adapter = this@JadwalKaByStasiunActivity.adapter
                        }
                    } else {
                        Toast.makeText(this@JadwalKaByStasiunActivity, "Tidak ada jadwal yang ditemukan", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        this@JadwalKaByStasiunActivity,
                        "Gagal mengambil data: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                } finally {
                    hideLoading() // ⬅️ Sembunyikan shimmer setelah data selesai dimuat
                }
            }
        }
    }

    private fun getStasiuName() {
        val stasiunBerangkat = intent.getParcelableExtra<Stasiun>("STASIUN_BRNGKT")
        val stasiunTujuan = intent.getParcelableExtra<Stasiun>("STASIUN_TUJUAN")

        binding.tvStasiunAsal.text = "${stasiunBerangkat?.nameStasiun} (${stasiunBerangkat?.kodeStasiun})"
        binding.tvStasiunTujuan.text = "${stasiunTujuan?.nameStasiun} (${stasiunTujuan?.kodeStasiun})"
    }

    private fun showLoading() {
        binding.apply {
            shimmerFrame.visibility = View.VISIBLE
            shimmerFrame.startShimmer()
        }
    }

    private fun hideLoading() {
        binding.apply {
            shimmerFrame.stopShimmer()
            shimmerFrame.visibility = View.GONE
        }
    }

    private fun toolbarSetup() {
        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    /**
     * 🔹 Fungsi untuk mengonversi String ke LocalTime
     */
    private fun parseTime(timeString: String): LocalTime {
        return try {
            LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm"))
        } catch (e: Exception) {
            LocalTime.MAX // Jika parsing gagal, beri nilai max agar tidak mengganggu sorting
        }
    }
}