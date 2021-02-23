package com.mezda.aciud.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mezda.aciud.data.models.Support
import com.mezda.aciud.databinding.RowSupportBinding

class SupportAdapter(): RecyclerView.Adapter<SupportAdapter.SupportHolder>() {

    var supportList = mutableListOf<Support>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SupportHolder {
        return SupportHolder.fromParent(parent)
    }

    override fun onBindViewHolder(holder: SupportHolder, position: Int) {
        holder.bind(supportList[position])
    }

    override fun getItemCount(): Int {
        return supportList.size
    }

    class SupportHolder(val binding: RowSupportBinding): RecyclerView.ViewHolder(binding.root){
        companion object {
            fun fromParent(parent: ViewGroup): SupportHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RowSupportBinding.inflate(layoutInflater,parent,false)
                return SupportHolder(binding)
            }
        }
        fun bind(support: Support){
            binding.supportTypeText.text = support.supportTypeName
        }
    }

    fun submit(list: MutableList<Support>) {
        this.supportList = list
        notifyDataSetChanged()
    }
}