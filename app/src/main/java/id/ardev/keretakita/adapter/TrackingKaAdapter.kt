package id.ardev.keretakita.adapter

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import id.ardev.keretakita.R
import id.ardev.keretakita.databinding.ItemTrackingKaBinding
import id.ardev.keretakita.model.data.Stop
import id.ardev.keretakita.utils.FormatHelper.calculateDuration
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Timer
import java.util.TimerTask

class TrackingKaAdapter(private val context: Context) :
    RecyclerView.Adapter<TrackingKaAdapter.TrackingKaViewHolder>() {

    private val handler = Handler(Looper.getMainLooper())
    private val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val runnable = object : Runnable {
        override fun run() {
            notifyDataSetChanged()
            handler.postDelayed(this, 1000) // Periksa setiap detik
        }
    }

    inner class TrackingKaViewHolder(val binding: ItemTrackingKaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(stop: Stop) {
            binding.apply {
                tvNamaStasiun.text = stop.station_name
                tvKedatangan.text = stop.arrival_time ?: "-"
                tvIsDeparted.text = "Scheduled"

                val (_, minutes) = calculateDuration(stop.arrival_time.toString(), stop.departure_time.toString())
                val durationText = "+$minutes mnt"
                tvDurasi.text = durationText

                // Memeriksa apakah waktu saat ini lebih besar dari departure_time
                val currentTime = dateFormat.format(Date())
                val departureTime = stop.departure_time ?: ""

                if (departureTime.isNotEmpty()) {
                    val currentDateTime = dateFormat.parse(currentTime)
                    val departureDateTime = dateFormat.parse(departureTime)

                    if (currentDateTime != null && departureDateTime != null && currentDateTime.after(departureDateTime)) {
                        lineTracking.setImageResource(R.drawable.line_departed)
                        tvIsDeparted.text = "Departed"
                    } else {
                        lineTracking.setImageResource(R.drawable.line_scheduled)
                        tvIsDeparted.text = "Scheduled"
                    }
                }
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Stop>() {
        override fun areItemsTheSame(oldItem: Stop, newItem: Stop): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Stop, newItem: Stop): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrackingKaAdapter.TrackingKaViewHolder {
        return TrackingKaViewHolder(
            ItemTrackingKaBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }

    override fun onBindViewHolder(
        holder: TrackingKaAdapter.TrackingKaViewHolder,
        position: Int
    ) {
        val stop = differ.currentList[position]
//        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
//        val isCurrentStop = determineCurrentStop(currentTime, position)

        holder.bind(stop)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Stop>) {
        differ.submitList(list)
        handler.post(runnable)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        handler.removeCallbacks(runnable) // Hentikan periksa waktu saat adapter tidak digunakan
    }

    private fun determineCurrentStop(currentTime: String, position: Int): Boolean {
        val stops = differ.currentList
        if (stops.isEmpty()) return false

        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentDateTime = format.parse(currentTime)

        if (position == 0) {
            // Jika stasiun pertama dan currentTime < arrival_time pertama
            val firstStopTime = stops[0].arrival_time?.let { format.parse(it) }
            return firstStopTime != null && currentDateTime.before(firstStopTime)
        }

        if (position == stops.size - 1) {
            // Jika stasiun terakhir dan currentTime < arrival_time terakhir
            val lastStopTime = stops[stops.size - 1].arrival_time?.let { format.parse(it) }
            return lastStopTime != null && currentDateTime.before(lastStopTime)
        }

        // Jika stasiun berada di tengah-tengah
        val currentStopTime = stops[position].arrival_time?.let { format.parse(it) }
        val nextStopTime = stops[position + 1].arrival_time?.let { format.parse(it) }

        return currentStopTime != null && nextStopTime != null &&
                currentDateTime.after(currentStopTime) && currentDateTime.before(nextStopTime)
    }
}