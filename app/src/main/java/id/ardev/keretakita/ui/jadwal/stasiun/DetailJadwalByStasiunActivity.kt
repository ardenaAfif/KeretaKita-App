package id.ardev.keretakita.ui.jadwal.stasiun

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dagger.hilt.android.AndroidEntryPoint
import id.ardev.keretakita.R
import id.ardev.keretakita.adapter.JadwalKaAdapter
import id.ardev.keretakita.databinding.ActivityDetailJadwalByStasiunBinding
import id.ardev.keretakita.model.data.JadwalKA

@AndroidEntryPoint
class DetailJadwalByStasiunActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailJadwalByStasiunBinding
    private var interstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailJadwalByStasiunBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbarAction()
        jadwalKaSetup()
        rvJadwalByStasiun()
        loadInterstitialAd()
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