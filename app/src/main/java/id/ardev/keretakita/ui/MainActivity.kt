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
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import id.ardev.keretakita.R
import id.ardev.keretakita.databinding.ActivityMainBinding
import id.ardev.keretakita.ui.home.HomeViewModel
import id.ardev.keretakita.ui.news.NewsActivity
import id.ardev.keretakita.utils.FormatHelper
import id.ardev.keretakita.utils.Resource
import kotlinx.coroutines.flow.collectLatest
import java.util.Calendar

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private val viewModel by viewModels<HomeViewModel>()

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
            menuBerita.setOnClickListener {
                Intent(this@MainActivity, NewsActivity::class.java).also {
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

        setupAutoCompleteTextView(searchStAsal, R.drawable.ic_depart)
        setupAutoCompleteTextView(searchStTujuan, R.drawable.ic_arrival)

        // == Button Cari jadwal ==
        btnCariJadwal.setOnClickListener {
            Toast.makeText(this, "OK!", Toast.LENGTH_SHORT).show()
        }

        dialog.show()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupAutoCompleteTextView(autoCompleteTextView: AutoCompleteTextView, iconRes: Int) {
        autoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(iconRes, 0, 0, 0)
        autoCompleteTextView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableRight = autoCompleteTextView.compoundDrawables[2]
                if (drawableRight != null &&
                    event.rawX >= (autoCompleteTextView.right - drawableRight.bounds.width())
                ) {
                    autoCompleteTextView.text.clear()
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
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
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