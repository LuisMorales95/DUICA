package com.mezda.aciud.ui.support.create

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.navigation.navGraphViewModels
import com.mezda.aciud.R
import com.mezda.aciud.databinding.FragmentNewSupportBinding
import com.mezda.aciud.ui.support.SupportFlowViewModelProvider
import com.mezda.aciud.ui.support.SupportViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class SupportNewFragment : Fragment(), View.OnClickListener{


    @Inject
    lateinit var factory: SupportFlowViewModelProvider
    private val viewModel by navGraphViewModels<SupportViewModel>(R.id.nav_support) {
        factory
    }
    private lateinit var binding: FragmentNewSupportBinding

    private lateinit var currentPhotoPath: String
    private var photoFile: File? = null
    private var photoUri: Uri? = null
    private var photoBitmap: Bitmap? = null
    private var photoEncoded: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_support, container, false)
        viewModel.supportType.observe(viewLifecycleOwner) {
            binding.supportTypeSpinner.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                it.toTypedArray()
            )
//            val supportTypePosition = viewModel.getSupportTypeIndex()
//            if (supportTypePosition != 0) binding.supportTypeSpinner.setSelection(
//                supportTypePosition
//            )
        }
        viewModel.onGetSupportType()
        binding.supportCameraButton.setOnClickListener(this)
        binding.supportImage.setOnClickListener(this)
        binding.supportSaveButton.setOnClickListener(this)
        return binding.root
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.supportCameraButton.id -> {}
            binding.supportImage.id -> {}
            binding.supportSaveButton.id -> {}
        }
    }
}