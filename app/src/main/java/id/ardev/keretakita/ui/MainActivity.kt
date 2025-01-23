package id.ardev.keretakita.ui

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
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import id.ardev.keretakita.R
import id.ardev.keretakita.databinding.ActivityMainBinding
import id.ardev.keretakita.utils.FormatHelper
import java.util.Calendar

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWelcomingText()
        startRealtimeClock()
        menuListener()
    }

    private fun menuListener() {
        binding.apply {
            menuJadwalKaSt.setOnClickListener {
                showMenuJadwalKaStDialog()
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
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

        var stasiun = arrayOf(
            "Purwokerto (PWT)",
            "Gambir (GMR)",
            "Surabaya Pasar Turi (SGI)",
            "Surabaya Gubeng (SGU)",
            "Yogyakarta (YK)",
            "Madiun (MN)"
        )
        var adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, stasiun)

        searchStAsal.setAdapter(adapter)
        searchStTujuan.setAdapter(adapter)

        // == search Stasiun Asal Setup ==
        searchStAsal.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.ic_depart, 0, 0, 0
        )

        // Fungsi untuk menampilkan/menyembunyikan tombol clear
        searchStAsal.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                // Periksa apakah drawable kanan (compoundDrawables[2]) tidak null
                val drawableRight = searchStAsal.compoundDrawables[2]
                if (drawableRight != null &&
                    event.rawX >= (searchStAsal.right - drawableRight.bounds.width())
                ) {
                    searchStAsal.text.clear() // Clear text
                    searchStAsal.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_depart, 0, 0, 0
                    ) // Hapus ikon close
                    return@setOnTouchListener true
                }
            }
            false
        }

        searchStAsal.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    searchStAsal.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_depart, 0, 0, 0
                    )
                } else {
                    searchStAsal.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_depart, 0, R.drawable.ic_close, 0
                    )
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // == search Stasiun Asal Setup ==
        searchStTujuan.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.ic_arrival, 0, 0, 0
        )

        // Fungsi untuk menampilkan/menyembunyikan tombol clear
        searchStTujuan.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                // Periksa apakah drawable kanan (compoundDrawables[2]) tidak null
                val drawableRight = searchStTujuan.compoundDrawables[2]
                if (drawableRight != null &&
                    event.rawX >= (searchStTujuan.right - drawableRight.bounds.width())
                ) {
                    searchStTujuan.text.clear() // Clear text
                    searchStTujuan.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_arrival, 0, 0, 0
                    ) // Hapus ikon close
                    return@setOnTouchListener true
                }
            }
            false
        }

        searchStTujuan.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    searchStTujuan.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_arrival, 0, 0, 0
                    )
                } else {
                    searchStTujuan.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_arrival, 0, R.drawable.ic_close, 0
                    )
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // == Button Cari jadwal ==
        btnCariJadwal.setOnClickListener {
            Toast.makeText(this, "OK!", Toast.LENGTH_SHORT).show()
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