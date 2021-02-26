package com.mezda.aciud.ui.support.image

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.mezda.aciud.R
import com.mezda.aciud.data.RetrofitModule
import com.mezda.aciud.databinding.FragmentNewSupportBinding
import com.mezda.aciud.ui.BaseFragment

class SupportImageFragment: BaseFragment() {

    lateinit var binding: FragmentNewSupportBinding
    private val args: SupportImageFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_support,container,false)
        binding.supportTypeSpinner.visibility = View.GONE
        binding.supportCameraButton.visibility = View.GONE
        Glide.with(requireContext()).load(RetrofitModule.baseUrl + args.imagePath.replace("~/", "")).into(binding.supportImage)
        binding.supportSaveButton.visibility = View.GONE
        return binding.root
    }
}