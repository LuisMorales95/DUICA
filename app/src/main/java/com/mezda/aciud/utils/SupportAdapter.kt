package com.mezda.aciud.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mezda.aciud.data.RetrofitModule
import com.mezda.aciud.data.models.Support
import com.mezda.aciud.databinding.RowSupportBinding

class SupportAdapter(val supportListener: SupportListener): RecyclerView.Adapter<SupportAdapter.SupportHolder>() {

    var supportList = mutableListOf<Support>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SupportHolder {
        return SupportHolder.fromParent(parent)
    }

    override fun onBindViewHolder(holder: SupportHolder, position: Int) {
        holder.bind(supportList[position], supportListener)
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
        fun bind(support: Support, supportListener: SupportListener){
            binding.supportTypeText.text = support.supportTypeName
            binding.supportDateText.text = support.date
            Glide.with(binding.root.context).load(RetrofitModule.baseUrl + support.image?.replace("~/", "")).into(binding.supportImage)
            binding.root.setOnClickListener {
                supportListener.clickSelect(support)
            }
            binding.supportTrashButton.setOnClickListener {
                supportListener.clickDelete(support)
            }
        }
    }

    fun submit(list: MutableList<Support>) {
        this.supportList = list
        notifyDataSetChanged()
    }

    class SupportListener(private val onClick:(support: Support) -> Unit, private val onDelete: (support: Support) -> Unit ){
        fun clickSelect(support: Support) = onClick(support)
        fun clickDelete(support: Support) = onDelete(support)
    }
}