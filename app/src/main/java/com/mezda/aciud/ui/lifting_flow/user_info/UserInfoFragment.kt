package com.mezda.aciud.ui.lifting_flow.user_info

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.Glide
import com.mezda.aciud.R
import com.mezda.aciud.databinding.FragmentUserInfoBinding
import com.mezda.aciud.ui.BaseFragment
import com.mezda.aciud.ui.lifting_flow.LiftingFlowViewModel
import com.mezda.aciud.ui.lifting_flow.LiftingFlowViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class UserInfoFragment : BaseFragment(), View.OnClickListener {

    private lateinit var binding: FragmentUserInfoBinding

    @Inject
    lateinit var factory: LiftingFlowViewModelProvider
    private val viewModel by navGraphViewModels<LiftingFlowViewModel>(R.id.liftingGraph) {
        factory
    }
    companion object {
        const val CAPTURE = 1
    }

    private lateinit var currentPhotoPath: String
    private var photoFile: File? = null
    private var photoUri: Uri? = null
    private var photoBitmap: Bitmap? = null
    private var photoEncoded: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_info, container, false)

        Glide.with(requireContext()).load(R.drawable.profile_picture)
            .into(binding.profilePictureImage)

        photoEncoded = viewModel.userInfo.picture_encoded
        viewModel.userInfo.picture_uri?.let {
            if (it != "null") {
                Timber.e(it)
                photoUri = Uri.parse(viewModel.userInfo.picture_uri)
                Glide.with(requireContext()).load(Uri.parse(it)).into(binding.profilePictureImage)
            }
        }
        binding.userNameText.editText?.setText(viewModel.userInfo.name)
        binding.userPaternalText.editText?.setText(viewModel.userInfo.paternal_last_name)
        binding.userMaternalText.editText?.setText(viewModel.userInfo.maternal_last_name)
        binding.userPhoneText.editText?.setText(viewModel.userInfo.phone_number)

        binding.profilePictureButton.setOnClickListener(this)
        binding.nextButton.setOnClickListener(this)

        return binding.root
    }

    private fun validateRequiredInfo(): Boolean {
        return when {
            binding.userNameText.editText?.text.toString().isEmpty() -> {
                binding.userNameText.error = "Requerido"
                false
            }
            binding.userPaternalText.editText?.text.toString().isEmpty() -> {
                binding.userPaternalText.error = "Requerido"
                false
            }
            binding.userMaternalText.editText?.text.toString().isEmpty() -> {
                binding.userMaternalText.error = "Requerido"
                false
            }
            else -> true
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            binding.profilePictureButton.id -> dispatchTakePictureIntent()
            binding.nextButton.id -> {
                if (validateRequiredInfo()) {
                    viewModel.saveUserInfo(
                        binding.userNameText.editText?.text.toString(),
                        binding.userPaternalText.editText?.text.toString(),
                        binding.userMaternalText.editText?.text.toString(),
                        binding.userPhoneText.editText?.text.toString(),
                        photoEncoded,
                        photoUri.toString()
                    )
                    launchDirection(UserInfoFragmentDirections.actionUserInfoFragmentToAddressInfoFragment())
                } else {
                    toast("Información Incompleta")
                }
            }
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
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
                    startActivityForResult(takePictureIntent, CAPTURE)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURE && resultCode == Activity.RESULT_OK) {
            photoBitmap = BitmapFactory.decodeFile(photoFile?.absolutePath)
//            photoBitmap = data?.extras?.get("data") as Bitmap
            val byteArrayOutputStream = ByteArrayOutputStream()
            photoBitmap?.let {
                it.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
                photoEncoded =
                    Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)
            }
            Glide.with(requireContext()).load(photoUri).into(binding.profilePictureImage)
        }

    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat(this.getString(R.string.date_hour_format), Locale.getDefault()).format(Date())
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

}