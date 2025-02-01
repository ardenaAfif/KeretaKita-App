package id.ardev.keretakita.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import id.ardev.keretakita.databinding.ItemTrackingKaBinding
import id.ardev.keretakita.model.data.Stop

class TrackingKaAdapter(private val context: Context) :
    RecyclerView.Adapter<TrackingKaAdapter.TrackingKaViewHolder>() {

    inner class TrackingKaViewHolder(private val binding: ItemTrackingKaBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(stop: Stop) {
            binding.apply {
                tvNamaStasiun.text = stop.station_name
                tvKedatangan.text = stop.arrival_time ?: "-"
                tvIsDeparted.text =  "Scheduled"
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
        holder.bind(stop)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Stop>) {
        differ.submitList(list)
    }
}