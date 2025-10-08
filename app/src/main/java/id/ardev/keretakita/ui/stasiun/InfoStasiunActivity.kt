package id.ardev.keretakita.ui.stasiun

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint
import id.ardev.keretakita.R
import id.ardev.keretakita.adapter.InfoStasiunAdapter
import id.ardev.keretakita.databinding.ActivityInfoStasiunBinding
import id.ardev.keretakita.ui.home.HomeViewModel
import id.ardev.keretakita.utils.Resource

@AndroidEntryPoint
class InfoStasiunActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInfoStasiunBinding
    private val viewModel by viewModels<HomeViewModel>()
    private lateinit var stasiunAdapter: InfoStasiunAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoStasiunBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            binding.toolbar.setPadding(0, systemBars.top, 0, 0)
            insets
        }

        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        toolbarSetup()
        getStasiunInfo()
        searchAction()
        rvSetup()
    }

    private fun toolbarSetup() {
        setSupportActionBar(binding.toolbar)

        // Mengaktifkan tombol kembali
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun searchAction() {
        binding.apply {
            search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    showLoading()
                    performSearch(query)
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    performSearch(newText)
                    return true
                }

            })
        }
    }

    private fun performSearch(query: String?) {
        val filteredKamus = if (!query.isNullOrBlank()) {
            viewModel.stasiunList.value.data?.filter { kamus ->
                kamus.nameStasiun.contains(query, ignoreCase = true)
            } ?: emptyList()
        } else {
            // Jika query kosong,tampilkan semua produk
            viewModel.stasiunList.value.data ?: emptyList()
        }

        stasiunAdapter.differ.submitList(filteredKamus)

    }

    private fun rvSetup() {
        stasiunAdapter = InfoStasiunAdapter(this)
        binding.rvStasiunList.apply {
            layoutManager = LinearLayoutManager(this@InfoStasiunActivity)
            adapter = stasiunAdapter
        }
    }

    private fun getStasiunInfo() {
        lifecycleScope.launchWhenStarted {
            viewModel.stasiunList.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        showLoading()
                    }
                    is Resource.Success -> {
                        hideLoading()
                        stasiunAdapter.differ.submitList(resource.data)
                    }
                    is Resource.Error -> {
                        hideLoading()
                    }
                    else -> Unit
                }
            }
        }
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

    override fun onSupportNavigateUp(): Boolean {
        // Menangani klik tombol kembali
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onPause() {
        binding.adView.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        binding.adView.resume()
    }

    override fun onDestroy() {
        binding.adView.destroy()
        super.onDestroy()
    }
}