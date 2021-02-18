package com.mezda.aciud.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mezda.aciud.R
import com.mezda.aciud.data.models.Operators
import com.mezda.aciud.databinding.RowUserInfoBinding
import java.util.zip.Inflater

class OperatorsAdapter(val operatorListener: OperatorListener) : RecyclerView.Adapter<OperatorsAdapter.OperatorViewHolder>() {

    private var operatorList = mutableListOf<Operators>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OperatorViewHolder {
        return OperatorViewHolder.fromParent(parent)
    }

    override fun onBindViewHolder(holder: OperatorViewHolder, position: Int) {
        holder.bind(operatorList[position], operatorListener)
    }

    override fun getItemCount(): Int {
        return operatorList.size
    }

    class OperatorViewHolder(val binding: RowUserInfoBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun fromParent(parent: ViewGroup): OperatorViewHolder {
                val binding = RowUserInfoBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return OperatorViewHolder(binding)
            }

            fun getSmileOrFrown(validator: Boolean): Int {
                return if (validator) R.drawable.ic_smile else R.drawable.ic_frown
            }
        }

        fun bind(operators: Operators, operatorListener: OperatorListener) {
            binding.identifierText.text = operators.operatorId.toString()
            binding.nameText.text = operators.user
            binding.rolText.text = operators.name

            binding.activeOption.setImageResource(getSmileOrFrown(operators.active?:false))
            binding.adminOption.setImageResource(getSmileOrFrown(operators.isAdmin?:false))
            binding.captureOption.setImageResource(getSmileOrFrown(operators.allowCapture?:false))
            binding.modifyOption.setImageResource(getSmileOrFrown(operators.allowModification?:false))

            binding.activeOption.setOnClickListener {
                operatorListener.click(operators.apply {
                    active = !(active ?: false)
                })
            }
            binding.adminOption.setOnClickListener {
                operatorListener.click(operators.apply {
                    isAdmin = !(isAdmin ?: false)
                })
            }
            binding.captureOption.setOnClickListener {
                operatorListener.click(operators.apply {
                    allowCapture = !(allowCapture ?: false)
                })
            }
            binding.modifyOption.setOnClickListener {
                operatorListener.click(operators.apply {
                    allowModification = !(allowModification ?: false)
                })
            }
        }
    }

    fun submitList(list: MutableList<Operators>) {
        operatorList = list
        notifyDataSetChanged()
    }

    class OperatorListener(val onClick: (Operators)-> Unit) {
        fun click(operators: Operators)= onClick(operators)
    }
}