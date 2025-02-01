package id.ardev.keretakita.ui.home

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint
import id.ardev.keretakita.R
import id.ardev.keretakita.databinding.ActivityMainBinding
import id.ardev.keretakita.ui.about.AboutActivity
import id.ardev.keretakita.ui.jadwal.ka.DetailJadwalKaActivity
import id.ardev.keretakita.ui.jadwal.stasiun.JadwalKaByStasiunActivity
import id.ardev.keretakita.ui.news.NewsActivity
import id.ardev.keretakita.ui.stasiun.InfoStasiunActivity
import id.ardev.keretakita.utils.FormatHelper
import id.ardev.keretakita.utils.Resource
import kotlinx.coroutines.flow.collectLatest
import java.util.Calendar

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private val viewModel by viewModels<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        setupWelcomingText()
        startRealtimeClock()
        menuListener()
    }

    private fun menuListener() {
        binding.apply {
            menuJadwalKaSt.setOnClickListener {
                showMenuJadwalKaStDialog()
            }
            menuJadwalKa.setOnClickListener {
                showMenuJadwalKaDialog()
            }
            menuBerita.setOnClickListener {
                Intent(this@HomeActivity, NewsActivity::class.java).also {
                    startActivity(it)
                }
            }
            menuInfoSt.setOnClickListener {
                Intent(this@HomeActivity, InfoStasiunActivity::class.java).also {
                    startActivity(it)
                }
            }
            menuTentangKami.setOnClickListener {
                Intent(this@HomeActivity, AboutActivity::class.java).also {
                    startActivity(it)
                }
            }
        }
    }

    private fun showMenuJadwalKaStDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_jadwal_ka_st)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val searchStAsal: AutoCompleteTextView = dialog.findViewById(R.id.searchStAsal)
        val searchStTujuan: AutoCompleteTextView = dialog.findViewById(R.id.searchStTujuan)
        val btnCariJadwal: TextView = dialog.findViewById(R.id.btnCariJadwal)

        var stasiunAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ArrayList<String>())

        searchStAsal.setAdapter(stasiunAdapter)
        searchStTujuan.setAdapter(stasiunAdapter)
        searchStAsal.threshold = 1
        searchStTujuan.threshold = 1

        val tukerStasiun: ImageView = dialog.findViewById(R.id.tukerStasiun)

        tukerStasiun.setOnClickListener {
            it.animate()
                .rotationBy(360f)
                .setDuration(300)
                .start()

            val tempAsal = searchStAsal.text.toString()
            val tempTujuan = searchStTujuan.text.toString()

            searchStAsal.setText(tempTujuan)
            searchStTujuan.setText(tempAsal)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.stasiunList.collectLatest { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val stasiunNames = resource.data?.map { "${it.nameStasiun} (${it.kodeStasiun})"  } ?: emptyList()
                        stasiunAdapter.clear()
                        stasiunAdapter.addAll(stasiunNames)
                        stasiunAdapter.notifyDataSetChanged()
                        Log.d(">>StasiunData", "Data loaded: ${resource.data}")
                    }
                    is Resource.Error -> {
                        Log.e(">>Error", "Data loaded: ${resource.data}")
                    }
                    is Resource.Loading -> {
                    }
                    else -> Unit
                }
            }
        }

        setupAutoCompleteStasiun(searchStAsal, R.drawable.ic_depart)
        setupAutoCompleteStasiun(searchStTujuan, R.drawable.ic_arrival)

        // == Button Cari jadwal ==
        btnCariJadwal.setOnClickListener {
            val stAsal = searchStAsal.text.toString()
            val stTujuan = searchStTujuan.text.toString()

            if (stAsal.isNotEmpty() && stTujuan.isNotEmpty()) {
                lifecycleScope.launchWhenStarted {
                    viewModel.stasiunList.collectLatest { resource ->
                        when (resource) {
                            is Resource.Success -> {

                                val stBerangkat = resource.data?.find {
                                    "${it.nameStasiun} (${it.kodeStasiun})" == stAsal
                                }
                                val stTujuan = resource.data?.find {
                                    "${it.nameStasiun} (${it.kodeStasiun})" == stTujuan
                                }
                                if (stBerangkat != null && stTujuan != null) {
                                    val intent = Intent(this@HomeActivity, JadwalKaByStasiunActivity::class.java)
                                    intent.putExtra("STASIUN_BRNGKT", stBerangkat)
                                    intent.putExtra("STASIUN_TUJUAN", stTujuan)

                                    startActivity(intent)
//                                    dialog.dismiss()
                                } else {
                                    Toast.makeText(this@HomeActivity, "Tidak ada jadwal yang ditemukan", Toast.LENGTH_SHORT).show()
                                }
                            }
                            is Resource.Error -> {
                                Toast.makeText(this@HomeActivity, "Error: ${resource.message}", Toast.LENGTH_SHORT).show()
                            }
                            is Resource.Loading -> {
                                // Show loading indicator
                            }
                            else -> Unit
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Silakan pilih stasiun asal dan tujuan", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupAutoCompleteStasiun(autoCompleteTextView: AutoCompleteTextView, iconRes: Int) {
        autoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(iconRes, 0, 0, 0)

        // Saat pengguna memilih dari dropdown, disable AutoCompleteTextView
        autoCompleteTextView.setOnItemClickListener { _, _, _, _ ->
            autoCompleteTextView.isFocusable = false
            autoCompleteTextView.isFocusableInTouchMode = false
            autoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(iconRes, 0, R.drawable.ic_close, 0)
        }

        autoCompleteTextView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableRight = autoCompleteTextView.compoundDrawables[2]
                if (drawableRight != null &&
                    event.rawX >= (autoCompleteTextView.right - drawableRight.bounds.width())
                ) {
                    autoCompleteTextView.text.clear()
                    autoCompleteTextView.isFocusable = true
                    autoCompleteTextView.isFocusableInTouchMode = true
                    autoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(iconRes, 0, 0, 0)

                    return@setOnTouchListener true
                }
            }
            false
        }

        autoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    autoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(iconRes, 0, 0, 0)
                    Log.d(">>AutoComplete", "Input: $s")
                } else {
                    autoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(iconRes, 0, R.drawable.ic_close, 0)
                    autoCompleteTextView.postDelayed({
                        if (autoCompleteTextView.text.isNotEmpty()) {
                            autoCompleteTextView.showDropDown() // **Menampilkan dropdown hanya jika teks ada**
                        }
                    }, 100)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun showMenuJadwalKaDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_jadwal_ka)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val searchNamaKa: AutoCompleteTextView = dialog.findViewById(R.id.searchNamaKa)
        val btnCariJadwal: TextView = dialog.findViewById(R.id.btnCariJadwalKa)

        val namaKaAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ArrayList<String>())
        searchNamaKa.setAdapter(namaKaAdapter)

        searchNamaKa.threshold = 1

        setupAutoCompleteStasiun(searchNamaKa, R.drawable.ic_depart)

        lifecycleScope.launchWhenStarted {
            viewModel.jadwalList.collectLatest { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val names = resource.data?.map { "[${it.no_ka}] ${it.name_ka} (${it.lintas})" } ?: emptyList()
                        namaKaAdapter.clear()
                        namaKaAdapter.addAll(names)
                        namaKaAdapter.notifyDataSetChanged()
                        Log.d(">>KeretaData", "Data loaded: ${resource.data}")
                    }
                    is Resource.Error -> {
                        Log.e(">>Error", resource.message ?: "Error fetching data")
                    }
                    else -> Unit
                }
            }
        }

        // == Button Cari jadwal ==
        btnCariJadwal.setOnClickListener {
            val namaKa = searchNamaKa.text.toString()
            lifecycleScope.launchWhenStarted {
                viewModel.jadwalList.collectLatest { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            val selectedJadwal = resource.data?.find {
                                "[${it.no_ka}] ${it.name_ka} (${it.lintas})" == namaKa
                            }
                            if (selectedJadwal != null) {
                                val intent = Intent(this@HomeActivity, DetailJadwalKaActivity::class.java)
                                intent.putExtra("jadwalKa", selectedJadwal)
                                startActivity(intent)
                                dialog.dismiss()
                            } else {
                                Toast.makeText(this@HomeActivity, "Jadwal tidak ditemukan", Toast.LENGTH_SHORT).show()
                            }
                        }
                        else -> {
                            Toast.makeText(this@HomeActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        dialog.show()
    }

    private fun setupWelcomingText() {
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val (welcomingText, emoji) = when (currentHour) {
            in 5..11 -> "Selamat Pagi" to "☀️"
            in 12..14 -> "Selamat Siang" to "🌞"
            in 15..17 -> "Selamat Sore" to "🌅"
            else -> "Selamat Malam" to "🌙"
        }

        binding.apply {
            binding.tvWelcomingNow.text = "$welcomingText  $emoji"
        }
    }

    private fun startRealtimeClock() {
        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                binding.tvTimeNow.text = FormatHelper.formatDate()
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(runnable)
    }

    override fun onResume() {
        super.onResume()
        setupWelcomingText()
        startRealtimeClock()
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable) // Stop the handler when the activity is paused
    }
}