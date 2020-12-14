package com.mezda.aciud.utils.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mezda.aciud.R
import com.mezda.aciud.databinding.DialogLoadingBinding

class LoadingDialog(context: Context): Dialog(context) {

    lateinit var binding : DialogLoadingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.onCreate(savedInstanceState)
        setCancelable(false)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DataBindingUtil.inflate(LayoutInflater.from(this.context), R.layout.dialog_loading, null, false)
        setContentView(binding.root)
        window.apply {
            this?.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
            )
            this?.setBackgroundDrawableResource(R.color.transparent)
            this?.setGravity(Gravity.CENTER)
        }
    }

    fun bindings() = binding
}

/*
private lateinit var binding: LayoutReportErrorBinding

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setCancelable(false)
    requestWindowFeature(Window.FEATURE_NO_TITLE)
    binding = DataBindingUtil.inflate(LayoutInflater.from(this.context), R.layout.layout_report_error, null, false)
    setContentView(binding.root)
    window.apply {
        this?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
        )
        this?.setBackgroundDrawableResource(R.color.transparent)
        this?.setGravity(Gravity.CENTER)
    }

    binding.reportRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    binding.reportRecyclerView.setHasFixedSize(true)

    binding.reportBack.setOnClickListener(this)
    binding.reportContinue.setOnClickListener(this)
}

fun getBinding(): LayoutReportErrorBinding {
    return binding
}*/
