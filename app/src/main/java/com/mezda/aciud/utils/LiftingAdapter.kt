package com.mezda.aciud.utils

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mezda.aciud.R
import com.mezda.aciud.data.models.LiftingInfo
import com.mezda.aciud.databinding.RowLiftingItemBinding

class LiftingAdapter(private val liftingListener: LiftingListener) : RecyclerView.Adapter<LiftingAdapter.LiftingViewHolder>() {

    private var list = mutableListOf<LiftingInfo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiftingViewHolder {
        return LiftingViewHolder.getViewHolder(parent)
    }

    override fun onBindViewHolder(holder: LiftingViewHolder, position: Int) {
        holder.bind(list[position],liftingListener)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class LiftingViewHolder(val binding: RowLiftingItemBinding) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun getViewHolder(parent: ViewGroup): LiftingViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val rowLiftingItemBinding = RowLiftingItemBinding.inflate(inflater, parent,false)
                return LiftingViewHolder(rowLiftingItemBinding)
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(liftingInfo: LiftingInfo, liftingListener: LiftingListener) {
            binding.textView2.text = "${liftingInfo.name} ${liftingInfo.paternal_surname} ${liftingInfo.maternal_surname}"
            binding.textView3.text = "${liftingInfo.number} ${liftingInfo.street}"
            binding.imageMap.setOnClickListener {
                liftingListener.onMap(liftingInfo)
            }
            binding.imageEdit.setOnClickListener {
                liftingListener.onEdit(liftingInfo)
            }
        }
    }

    fun submit(list: MutableList<LiftingInfo>){
        this.list = list
        notifyDataSetChanged()
    }

    fun getCurrentItems(): MutableList<LiftingInfo> {
        return this.list
    }

    class LiftingListener(val editListener: (LiftingInfo) -> Unit, val mapListener: (LiftingInfo) -> Unit) {
        fun onEdit(liftingInfo: LiftingInfo) = editListener(liftingInfo)
        fun onMap(liftingInfo: LiftingInfo) = mapListener(liftingInfo)
    }
}