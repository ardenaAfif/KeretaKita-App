package id.ardev.keretakita.adapter

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import id.ardev.keretakita.R
import id.ardev.keretakita.databinding.ItemTrackingKaBinding
import id.ardev.keretakita.model.data.Stop
import java.lang.System.currentTimeMillis
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TrackingKaAdapter(private val context: Context) :
    RecyclerView.Adapter<TrackingKaAdapter.TrackingKaViewHolder>() {

    private val handler = Handler(Looper.getMainLooper())
//    private val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    private var recyclerView: RecyclerView? = null

    private val runnable = object : Runnable {
        override fun run() {
            updateVisibleItemStatus()
            handler.postDelayed(this, 1000) // Periksa setiap detik
        }
    }

    inner class TrackingKaViewHolder(val binding: ItemTrackingKaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(stop: Stop) {
            binding.apply {
                tvNamaStasiun.text = stop.station_name
                tvKedatangan.text = stop.arrival_time ?: "-"
//                tvIsDeparted.text = "Scheduled"

                stop.arrival_time?.let { arrival ->
                    stop.departure_time?.let { departure ->
                        val (_, minutes) = calculateDuration(arrival, departure)
                        val durationText = "+$minutes mnt"
                        tvDurasi.text = durationText
                    } ?: run { tvDurasi.text = "" }
                } ?: run { tvDurasi.text = "" }

                updateStatus(stop.departure_time)
            }
        }

        fun updateStatus(departureTimeString: String?) {
            binding.apply {
                if (departureTimeString.isNullOrEmpty()) {
                    lineTracking.setImageResource(R.drawable.line_scheduled)
                    tvIsDeparted.text = "Scheduled"
                    return
                }

                try {
                    // get Time now
                    val currentCalendar = Calendar.getInstance()
                    val currentHour = currentCalendar.get(Calendar.HOUR_OF_DAY)
                    val currentTimeMillis = currentCalendar.timeInMillis

                    // parse departure time
                    val timeParts = departureTimeString.split(":")
                    if (timeParts.size != 2) {
                        // Format waktu tidak valid
                        lineTracking.setImageResource(R.drawable.line_scheduled)
                        tvIsDeparted.text = "Invalid Time"
                        return
                    }

                    val departureHour = timeParts[0].toInt()
                    val departureMinute = timeParts[1].toInt()
//                    val departureTimeMillis = departureCalendar.timeInMillis

                    val departureCalendar = Calendar.getInstance()
                    departureCalendar.set(Calendar.HOUR_OF_DAY, departureHour)
                    departureCalendar.set(Calendar.MINUTE, departureMinute)
                    departureCalendar.set(Calendar.SECOND, 0)
                    departureCalendar.set(Calendar.MILLISECOND, 0)

                    val correctDepartureTimeMillis = departureCalendar.timeInMillis

                    Log.d("TrackingDebug", "Stop: ${binding.tvNamaStasiun.text}, DepartureString: $departureTimeString")
                    Log.d("TrackingDebug", "Current: H=$currentHour, MS=$currentTimeMillis")
//                    Log.d("TrackingDebug", "Departure: H=$departureHour, M=$departureMinute, MS=$departureTimeMillis (based on today)")

                    // --- Heuristik Pendeteksi Lintas Hari ---
                    // Jika waktu keberangkatan (<12) dan waktu sekarang sudah malam (>18),
                    // anggap keberangkatan adalah BESOK.
                    val isPotentiallyTomorrow = departureHour < 6 && currentHour > 18
                    Log.d("TrackingDebug", "isPotentiallyTomorrow: $isPotentiallyTomorrow")

                    // 4. Bandingkan waktu
                    if (isPotentiallyTomorrow) {
                        Log.d("TrackingDebug", "--> Status: Scheduled (Potentially Tomorrow)")
                        // Jika kemungkinan besar besok, statusnya pasti scheduled hari ini.
                        lineTracking.setImageResource(R.drawable.line_scheduled)
                        tvIsDeparted.text = "Scheduled"
                    } else {
                        // Jika bukan kasus lintas hari yang terdeteksi ATAU
                        // jika waktu saat ini dan waktu keberangkatan tampaknya di hari yang sama,
                        // lakukan perbandingan normal.
                        val comparisonResult = currentTimeMillis >= correctDepartureTimeMillis
                        Log.d("TrackingDebug", "Comparison (Current >= Departure Today): $comparisonResult")
                        if (comparisonResult) {
                            Log.d("TrackingDebug", "--> Status: Departed")
                            lineTracking.setImageResource(R.drawable.line_departed)
                            tvIsDeparted.text = "Departed"
                        } else {
                            Log.d("TrackingDebug", "--> Status: Scheduled")
                            lineTracking.setImageResource(R.drawable.line_scheduled)
                            tvIsDeparted.text = "Scheduled"
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    lineTracking.setImageResource(R.drawable.line_scheduled)
                    tvIsDeparted.text = "Error"
                }
            }

        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Stop>() {
        override fun areItemsTheSame(oldItem: Stop, newItem: Stop): Boolean {
            return oldItem.station_kode == newItem.station_kode
        }

        override fun areContentsTheSame(oldItem: Stop, newItem: Stop): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrackingKaViewHolder {
        return TrackingKaViewHolder(
            ItemTrackingKaBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }

    override fun onBindViewHolder(
        holder: TrackingKaViewHolder,
        position: Int
    ) {
        val stop = differ.currentList[position]
        holder.bind(stop)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun submitList(list: List<Stop>) {
        differ.submitList(list) {
            if (list.isNotEmpty() && handler.hasCallbacks(runnable).not()) {
                handler.post(runnable)
            } else if (list.isEmpty()) {
                handler.removeCallbacks(runnable)
            }
        }
        handler.post(runnable)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
        if (differ.currentList.isNotEmpty()) {
            handler.removeCallbacks(runnable)
            handler.post(runnable)
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        handler.removeCallbacks(runnable) // Hentikan periksa waktu saat adapter tidak digunakan
        this.recyclerView = null
    }

    private fun updateVisibleItemStatus() {
        recyclerView?.let { rv ->
            val layoutManager = rv.layoutManager ?: return
            val firstVisible =
                (layoutManager as? androidx.recyclerview.widget.LinearLayoutManager)?.findFirstVisibleItemPosition()
                    ?: 0
            val lastVisible =
                (layoutManager as? androidx.recyclerview.widget.LinearLayoutManager)?.findLastVisibleItemPosition()
                    ?: (itemCount - 1)

            // Hanya update item yang terlihat (atau sedikit di luarnya)
            for (i in firstVisible..lastVisible) {
                if (i < 0 || i >= differ.currentList.size) continue // Batas aman

                val holder = rv.findViewHolderForAdapterPosition(i) as? TrackingKaViewHolder
                holder?.let {
                    val stop = differ.currentList[i]
                    it.updateStatus(stop.departure_time) // Panggil update status di ViewHolder
                }
            }
        } ?: run {
            // Fallback jika RecyclerView null (seharusnya tidak terjadi jika lifecycle benar)
            // Anda bisa panggil notifyDataSetChanged() di sini sebagai fallback terakhir,
            // tapi idealnya lifecycle di atas sudah cukup.
            // notifyDataSetChanged()
        }
    }

    private fun calculateDuration(timeBerangkat: String, timeTujuan: String): Pair<Int, Int> {
        // Implementasi perhitungan durasi kamu di sini
        // Pastikan menghandle format HH:mm dan kemungkinan lintas hari jika perlu
        // Contoh sederhana (HANYA UNTUK ILUSTRASI, mungkin perlu disesuaikan):
        try {
            val format = SimpleDateFormat("HH:mm", Locale.getDefault())
            val dateBerangkat = format.parse(timeBerangkat)
            val dateTujuan = format.parse(timeTujuan)

            var diff = dateTujuan.time - dateBerangkat.time // Milliseconds

            // Handle jika tujuan keesokan harinya (estimasi sederhana)
            if (diff < 0) {
                diff += 24 * 60 * 60 * 1000 // Tambah 1 hari dalam milidetik
            }

            val minutes = (diff / (60 * 1000)) % 60
            val hours = (diff / (60 * 60 * 1000)) // Total jam, bisa > 24 jika lintas hari

            // Jika hanya perlu menit untuk tampilan +X mnt
            val totalMinutes = diff / (60 * 1000)

            // Sesuaikan return value dengan kebutuhan tvDurasi di bind()
            // Jika tvDurasi hanya butuh menit antar arrival & departure di stop yg sama:
            val formatSimple = SimpleDateFormat("HH:mm", Locale.getDefault())
            val arrivalDate = formatSimple.parse(timeBerangkat) // Anggap timeBerangkat = arrival_time
            val departureDate = formatSimple.parse(timeTujuan) // Anggap timeTujuan = departure_time
            var diffStop = departureDate.time - arrivalDate.time
            if (diffStop < 0) diffStop += 24 * 60 * 60 * 1000 // Lintas hari di stasiun? Jarang terjadi tapi mungkin
            val minutesStop = (diffStop / (60 * 1000)).toInt()
            return Pair(0, minutesStop) // Kembalikan 0 jam dan hanya menit

        } catch (e: Exception) {
            return Pair(0, 0) // Error case
        }
    }
}