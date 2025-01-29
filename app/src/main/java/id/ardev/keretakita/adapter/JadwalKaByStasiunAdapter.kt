package id.ardev.keretakita.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import id.ardev.keretakita.R
import id.ardev.keretakita.databinding.ItemJadwalKaBinding
import id.ardev.keretakita.databinding.ItemKeretaListBinding
import id.ardev.keretakita.model.data.JadwalKA
import id.ardev.keretakita.utils.FormatHelper.calculateDuration
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class JadwalKaByStasiunAdapter(
    private val context: Context,
    private val jadwalList: List<JadwalKA>,
    private val stasiunBerangkatUser: String, // Stasiun berangkat yang dipilih user
    private val stasiunTujuanUser: String // Stasiun tujuan yang dipilih user
)  :
    RecyclerView.Adapter<JadwalKaByStasiunAdapter.JadwalKaByStasiunViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            JadwalKaByStasiunViewHolder {
        return JadwalKaByStasiunViewHolder(
            ItemKeretaListBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
    }

    override fun getItemCount(): Int = jadwalList.size

    override fun onBindViewHolder(holder: JadwalKaByStasiunViewHolder, position: Int) {
        holder.bind(jadwalList[position], stasiunBerangkatUser, stasiunTujuanUser)
    }

    inner class JadwalKaByStasiunViewHolder(private val binding: ItemKeretaListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(jadwal: JadwalKA, stasiunBerangkatUser: String, stasiunTujuanUser: String) {
            binding.apply {
                namaKa.text = "${jadwal.name_ka} [${jadwal.no_ka}]"

                val stopBerangkat = jadwal.stops.firstOrNull { it.station_name == stasiunBerangkatUser }
                val stopTujuan = jadwal.stops.firstOrNull { it.station_name == stasiunTujuanUser }

                waktuBerangkat.text = stopBerangkat?.departure_time ?: "-"
                stasiunBerangkat.text = stopBerangkat?.station_name ?: "-"

                waktuTujuan.text = stopTujuan?.arrival_time ?: "-"
                stasiunTujuan.text = stopTujuan?.station_name ?: "-"

                val (hours, minutes) = calculateDuration(stopBerangkat?.departure_time.toString(), stopTujuan?.arrival_time.toString())
                val durationText = "$hours jam $minutes mnt"
                tvDurasi.text = durationText

                itemView.setOnClickListener {
                    Toast.makeText(context, "KA ${jadwal.name_ka}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}