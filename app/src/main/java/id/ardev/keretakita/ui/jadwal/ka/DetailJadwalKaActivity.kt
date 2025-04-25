package id.ardev.keretakita.ui.jadwal.ka

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dagger.hilt.android.AndroidEntryPoint
import id.ardev.keretakita.adapter.JadwalKaAdapter
import id.ardev.keretakita.databinding.ActivityDetailJadwalKaBinding
import id.ardev.keretakita.model.data.JadwalKA
import id.ardev.keretakita.utils.FormatHelper.calculateDuration

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class DetailJadwalKaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailJadwalKaBinding
    private lateinit var jadwalKaAdapter: JadwalKaAdapter
    private var jadwalKa: JadwalKA? = null
    private var interstitialAd: InterstitialAd? = null

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
        loadInterstitialAd()
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
            tvKelasKa.text = jadwalKa.kelas_ka
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

    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
//            "ca-app-pub-3940256099942544/1033173712", // test ads
            "ca-app-pub-3376466499193547/3833234576",
            adRequest,
            object : InterstitialAdLoadCallback(){
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                }
            }
        )
    }

    private fun showInterstitialAdAndExit() {
        if (interstitialAd != null) {
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null // Reset iklan setelah ditutup
                    loadInterstitialAd()
                    finish() // Tutup aktivitas setelah iklan selesai
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    finish() // Jika gagal, langsung tutup aktivitas
                }
            }
            interstitialAd?.show(this)
        } else {
            finish() // Jika iklan belum siap, langsung keluar
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        showInterstitialAdAndExit()
        return true
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        showInterstitialAdAndExit()
    }
}