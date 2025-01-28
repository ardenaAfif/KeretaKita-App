package id.ardev.keretakita.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import id.ardev.keretakita.databinding.ItemJadwalKaBinding
import id.ardev.keretakita.model.data.JadwalKA
import id.ardev.keretakita.model.data.Stop
import id.ardev.keretakita.utils.FormatHelper.calculateDuration
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class JadwalKaAdapter(private val context: Context) :
    RecyclerView.Adapter<JadwalKaAdapter.JadwalKaViewHolder>() {

    inner class JadwalKaViewHolder(private val binding: ItemJadwalKaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(stop: Stop) {
            binding.apply {
                tvNamaStasiun.text = stop.station_name
                tvJadwalDatang.text = stop.arrival_time ?: "-"
                tvJadwalBerangkat.text = stop.departure_time ?: "-"

                val (_, minutes) = calculateDuration(stop.arrival_time.toString(), stop.departure_time.toString())
                val durationText = "$minutes mnt"
                tvDurasi.text = durationText
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
    ): JadwalKaViewHolder {
        return JadwalKaViewHolder(
            ItemJadwalKaBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: JadwalKaViewHolder, position: Int) {
        val stop = differ.currentList[position]
        holder.bind(stop)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Stop>) {
        differ.submitList(list)
    }
}