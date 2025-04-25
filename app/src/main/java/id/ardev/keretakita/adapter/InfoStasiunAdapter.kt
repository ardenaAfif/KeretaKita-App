package id.ardev.keretakita.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import id.ardev.keretakita.databinding.ItemStasiunListBinding
import id.ardev.keretakita.model.data.Stasiun
import id.ardev.keretakita.ui.stasiun.DetailInfoStasiunActivity


class InfoStasiunAdapter(private val context: Context) :
    RecyclerView.Adapter<InfoStasiunAdapter.InfoStasiunViewHolder>() {

    inner class InfoStasiunViewHolder(private val binding: ItemStasiunListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(stasiun: Stasiun) {
            binding.apply {
                namaStasiun.text = "${ stasiun.nameStasiun } [${ stasiun.kodeStasiun }]"

                itemView.setOnClickListener {
                    val intent = Intent(context, DetailInfoStasiunActivity::class.java).apply {
                        putExtra("EXTRA_STASIUN", stasiun)
                    }
                    context.startActivity(intent)
                }
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Stasiun>() {
        override fun areItemsTheSame(oldItem: Stasiun, newItem: Stasiun): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Stasiun, newItem: Stasiun): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InfoStasiunViewHolder {
       return InfoStasiunViewHolder(
           ItemStasiunListBinding.inflate(
               LayoutInflater.from(context), parent, false
           )
       )
    }

    override fun onBindViewHolder(holder: InfoStasiunViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}