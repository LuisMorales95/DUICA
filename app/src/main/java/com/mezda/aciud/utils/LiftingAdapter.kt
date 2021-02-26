package com.mezda.aciud.utils

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mezda.aciud.R
import com.mezda.aciud.data.models.LiftingInfo
import com.mezda.aciud.databinding.RowLiftingItemBinding

class LiftingAdapter(private val liftingListener: LiftingListener) : RecyclerView.Adapter<LiftingAdapter.LiftingViewHolder>(), Filterable {

    private var list = mutableListOf<LiftingInfo>()
    private var filteredList : MutableList<LiftingInfo> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiftingViewHolder {
        return LiftingViewHolder.getViewHolder(parent)
    }

    override fun onBindViewHolder(holder: LiftingViewHolder, position: Int) {
        holder.bind(filteredList[position],liftingListener)
    }

    override fun getItemCount(): Int {
        return filteredList.size
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
        fun bind(liftingInfo: LiftingInfo, liftingListener: LiftingListener ) {
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
        filteredList = this.list
        notifyDataSetChanged()
    }

    fun getCurrentItems(): MutableList<LiftingInfo> {
        return this.list
    }

    class LiftingListener(private  val selectedItem: (LiftingInfo) -> Unit) {
        fun onSelectItem(liftingInfo: LiftingInfo) = selectedItem(liftingInfo)
    }

    @Suppress("UNCHECKED_CAST")
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val string = p0.toString()
                filteredList = if (string.isEmpty()) {
                    list
                } else {
                    val filterResult = mutableListOf<LiftingInfo>()
                    for (row in list) {
                        if (row.fullName().toUpperCase().contains(string.toUpperCase())) {
                            filterResult.add(row)
                        }
                    }
                    filterResult
                }
                val filteredResult = FilterResults()
                filteredResult.values = filteredList
                return filteredResult
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                filteredList = p1?.values as MutableList<LiftingInfo>
                notifyDataSetChanged()
            }
        }
    }
}