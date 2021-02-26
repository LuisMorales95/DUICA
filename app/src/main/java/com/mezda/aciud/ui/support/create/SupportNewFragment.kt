package com.mezda.aciud.ui.support.create

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.Glide
import com.mezda.aciud.R
import com.mezda.aciud.databinding.FragmentNewSupportBinding
import com.mezda.aciud.ui.BaseFragment
import com.mezda.aciud.ui.lifting_flow.preview_info.PreviewInfoFragmentDirections
import com.mezda.aciud.ui.lifting_flow.user_info.UserInfoFragment
import com.mezda.aciud.ui.support.SupportFlowViewModelProvider
import com.mezda.aciud.ui.support.SupportViewModel
import com.mezda.aciud.utils.dialogs.LoadingDialog
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SupportNewFragment : BaseFragment(), View.OnClickListener{

    companion object {
        const val CAPTURE_SUPPORT = 2
    }

    @Inject
    lateinit var factory: SupportFlowViewModelProvider
    private val viewModel by navGraphViewModels<SupportViewModel>(R.id.nav_support) {
        factory
    }
    private lateinit var binding: FragmentNewSupportBinding

    private lateinit var loadingDialog: LoadingDialog
    private lateinit var currentPhotoPath: String
    private var photoFile: File? = null
    private var photoUri: Uri? = null
    private var photoBitmap: Bitmap? = null
    private var photoEncoded: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        loadingDialog = LoadingDialog(requireActivity())
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_support, container, false)
        viewModel.loading.observe(viewLifecycleOwner,{
            if (it){
                loadingDialog.apply {
                    show()
                    binding.apply {
                        this.messageText.text = "Enviando"
                    }
                }
            } else {
                loadingDialog.dismiss()
            }
        })
        viewModel.messages.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.messageShown()
            }
        })
        viewModel.registerSuccess.observe(viewLifecycleOwner, {
            if (it.get() == true) {
                popBack()
                viewModel.registerSuccessful()
            }
        })

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
        binding.supportSaveButton.setOnClickListener(this)
        return binding.root
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.supportCameraButton.id -> {
                dispatchTakePictureIntent()
            }
            binding.supportSaveButton.id -> {
                if (validateRequiredInfo()) {
                    viewModel.onSaveSupport(binding.supportTypeSpinner.selectedItemPosition, photoEncoded)
                }
            }
        }
    }

    private fun validateRequiredInfo(): Boolean {
        return when {
            binding.supportTypeSpinner.selectedItemPosition == 0 -> {
                toast("Tipo de Apoyo faltante")
                false
            }
            (photoEncoded ?: "").isEmpty() -> {
                toast("Foto faltante")
                false
            }
            else -> true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURE_SUPPORT && resultCode == Activity.RESULT_OK) {
            photoBitmap = BitmapFactory.decodeFile(photoFile?.absolutePath)
            val byteArrayOutputStream = ByteArrayOutputStream()
            photoBitmap?.let {
                it.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
                photoEncoded = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)
            }
            Glide.with(requireContext()).load(photoUri).into(binding.supportImage)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat(this.getString(R.string.date_hour_format), Locale.getDefault()).format(
            Date()
        )
        val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    photoFile = createImageFile()
                    photoFile
                } catch (ex: IOException) {
                    Toast.makeText(requireContext(), ex.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    photoUri = FileProvider.getUriForFile(
                        requireContext(),
                        getString(R.string.file_provider_package),
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    startActivityForResult(takePictureIntent, CAPTURE_SUPPORT)
                }
            }
        }
    }
}