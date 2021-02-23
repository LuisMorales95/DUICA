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
    private var permissionModify = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiftingViewHolder {
        return LiftingViewHolder.getViewHolder(parent)
    }

    override fun onBindViewHolder(holder: LiftingViewHolder, position: Int) {
        holder.bind(list[position],liftingListener, permissionModify)
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
        fun bind(liftingInfo: LiftingInfo, liftingListener: LiftingListener, permissionModify: Boolean) {
            binding.textView4.text = "${liftingInfo.idLifting}"
            binding.textView2.text = "${liftingInfo.name} ${liftingInfo.paternal_surname} ${liftingInfo.maternal_surname}"
            binding.textView3.text = "${liftingInfo.number} ${liftingInfo.street}"
            binding.root.setOnClickListener {
                liftingListener.onSelectItem(liftingInfo)
            }
        }
    }

    fun submit(list: MutableList<LiftingInfo>){
        this.list = list
        notifyDataSetChanged()
    }

    fun setPermissionModify(modify: Boolean){
        this.permissionModify = modify
        notifyDataSetChanged()
    }

    fun getCurrentItems(): MutableList<LiftingInfo> {
        return this.list
    }

    class LiftingListener(private  val selectedItem: (LiftingInfo) -> Unit) {
        fun onSelectItem(liftingInfo: LiftingInfo) = selectedItem(liftingInfo)
    }
}