@file:Suppress("DEPRECATION")

package com.mezda.aciud.ui.lifting

import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputFilter
import android.util.Base64
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.mezda.aciud.R
import com.mezda.aciud.data.GPSTracker
import com.mezda.aciud.data.models.Locality
import com.mezda.aciud.databinding.FragmentLiftingBinding
import com.mezda.aciud.ui.BaseFragment
import com.mezda.aciud.utils.SuburbAutoCompleteValidator
import com.mezda.aciud.utils.SuburbFocusListener
import com.mezda.aciud.utils.dialogs.LoadingDialog
import com.mezda.aciud.utils.section.SectionAutoCompleteValidator
import com.mezda.aciud.utils.section.SectionFocusListener
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class LiftingFragment : BaseFragment(), View.OnClickListener {

    private lateinit var binding: FragmentLiftingBinding
    private val liftingViewModel by viewModels<LiftingViewModel>()

    private lateinit var gpsTracker: GPSTracker
    private var sympathizer_enabled = false
    lateinit var currentPhotoPath: String

    private var REQUEST_IMAGE_CAPTURE = 1
    private var photoFile: File? = null
    private var photoUri: Uri? = null
    private var photoBitmap: Bitmap? = null
    private var photoEncoded: String? = null

    private lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        gpsTracker = GPSTracker(requireContext())
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_lifting, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        Glide.with(requireContext()).load(R.drawable.empty_avatar).into(binding.pictureImageView)

        loadingDialog = LoadingDialog(requireActivity())

        liftingViewModel.loading.observe(viewLifecycleOwner,{
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

        liftingViewModel.localities.observe(viewLifecycleOwner, {
            binding.localitySpinner.adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    it.toTypedArray()
            )
        })

        liftingViewModel.suburb.observe(viewLifecycleOwner, {
            val adapter: ArrayAdapter<String> = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    it.toTypedArray()
            )
            binding.suburbAutoComplete.filters = arrayOf<InputFilter>(InputFilter.AllCaps())
            binding.suburbAutoComplete.setAdapter(adapter)
            binding.suburbAutoComplete.validator = SuburbAutoCompleteValidator(it)
            binding.suburbAutoComplete.onFocusChangeListener = SuburbFocusListener()
            /*binding.suburbSpinner.adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    it.toTypedArray()
            )*/
        })

        liftingViewModel.section.observe(viewLifecycleOwner) {
            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    it.toTypedArray()
            )
//            binding.sectionSpinner.adapter = adapter
            binding.sectionAutoComplete.setAdapter(adapter)
            binding.sectionAutoComplete.validator = SectionAutoCompleteValidator(it)
            binding.sectionAutoComplete.onFocusChangeListener = SectionFocusListener()
        }


        liftingViewModel.profession.observe(viewLifecycleOwner) {
            binding.professionSpinner.adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    it.toTypedArray()
            )
        }

        liftingViewModel.supportType.observe(viewLifecycleOwner) {
            binding.supportTypeSpinner.adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    it.toTypedArray()
            )
        }

        liftingViewModel.flags.observe(viewLifecycleOwner) {
            binding.flagSpinner.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                it.toTypedArray()
            )
        }
        liftingViewModel.supervisor.observe(viewLifecycleOwner, {
            Timber.e("supervisor ${Gson().toJson(it)}")
            binding.supervisorText.text = it?.name
        })

        liftingViewModel.operator.observe(viewLifecycleOwner, {
            Timber.e("operator ${Gson().toJson(it)}")
            binding.operatorText.text = it?.name
        })

        liftingViewModel.registerSuccess.observe(viewLifecycleOwner, {
            if (it.get() == true) {
                popBack()
                liftingViewModel.registerSuccessful()
            }
        })

        binding.localitySpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        liftingViewModel.onLocalitySelected(p2)
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {}
                }

        binding.gpsLocationButton.setOnClickListener(this)
        binding.registerButton.setOnClickListener(this)
        binding.pictureButton.setOnClickListener(this)
        binding.pictureImageView.setOnClickListener(this)
        binding.sympathizerSwitch.setOnCheckedChangeListener { _, b ->
            sympathizer_enabled = b
        }

        liftingViewModel.onGetSupervisor()
//        liftingViewModel.onGetLocalities()
        binding.localityTextView.text = Locality.getDefault().nameLocality
        binding.localitySpinner.visibility = View.GONE
        liftingViewModel.onLocalityByDefault()
        liftingViewModel.onGetOperator()
        liftingViewModel.onGetSections()
        liftingViewModel.onGetProfessions()
        liftingViewModel.onGetSupportType()
        liftingViewModel.onGetFlags()

        gpsTracker.location()
        return binding.root
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            binding.gpsLocationButton.id -> {
                if (gpsTracker.canGetLocation()) {
                    val latitude: Double = gpsTracker.latitude
                    val longitude: Double = gpsTracker.longitude
                    binding.latitudeText.text = "$latitude"
                    binding.longitudeText.text = "$longitude"
                } else {
                    gpsTracker.showSettingsAlert()
                }
            }
            binding.pictureImageView.id -> {

            }
            binding.pictureButton.id -> {
                dispatchTakePictureIntent()
            }
            binding.registerButton.id -> {
                if (validateForm()) {
                    liftingViewModel.sendLiftingInfo(
                            binding.nameText.editText?.text.toString(),
                            binding.paternalSurnameText.editText?.text.toString(),
                            binding.maternalSurnameText.editText?.text.toString(),
                            binding.phoneNumberText.editText?.text.toString(),
                            binding.streetText.editText?.text.toString(),
                            binding.streetNumberText.editText?.text.toString(),
                            binding.suburbAutoComplete.text.toString(),
                            binding.latitudeText.text.toString(),
                            binding.longitudeText.text.toString(),
                            binding.sectionAutoComplete.text.toString(),
                            binding.professionSpinner.selectedItemPosition,
                            binding.flagSpinner.selectedItemPosition,
                            binding.observationsTextArea.text.toString(),
                            sympathizer_enabled,
                            photoEncoded
                    )
                }
            }
        }
    }

    private fun validateForm(): Boolean {
        binding.nameText.isErrorEnabled = false
        binding.paternalSurnameText.isErrorEnabled = false
        binding.maternalSurnameText.isErrorEnabled = false
        binding.phoneNumberText.isErrorEnabled = false
        binding.streetText.isErrorEnabled = false
        binding.streetNumberText.isErrorEnabled = false

        return when {
            binding.nameText.editText?.text.toString().isEmpty() -> {
                binding.nameText.error = "Requerido"
                false
            }
            binding.paternalSurnameText.editText?.text.toString().isEmpty() -> {
                binding.paternalSurnameText.error = "Requerido"
                false
            }
            binding.maternalSurnameText.editText?.text.toString().isEmpty() -> {
                binding.maternalSurnameText.error = "Requerido"
                false
            }
            binding.phoneNumberText.editText?.text.toString().isEmpty() -> {
                binding.phoneNumberText.error = "Requerido"
                false
            }
            binding.streetText.editText?.text.toString().isEmpty() -> {
                binding.streetText.error = "Requerido"
                false
            }
            binding.streetNumberText.editText?.text.toString().isEmpty() -> {
                binding.streetNumberText.error = "Requerido"
                false
            }
            binding.suburbAutoComplete.text.toString().isEmpty() -> {
                binding.streetNumberText.error = "Requerido"
                false
            }
            binding.professionSpinner.selectedItemPosition == 0 -> {
                Toast.makeText(requireContext(), "Profession faltante", Toast.LENGTH_SHORT)
                        .show()
                false
            }
            binding.supportTypeSpinner.selectedItemPosition == 0 -> {
                Toast.makeText(requireContext(), "Tipo de Apoyo faltante", Toast.LENGTH_SHORT)
                        .show()
                false
            }
            binding.flagSpinner.selectedItemPosition == 0 -> {
                Toast.makeText(requireContext(), "Estado faltante", Toast.LENGTH_SHORT)
                    .show()
                false
            }
            binding.latitudeText.text.toString().isEmpty() || binding.longitudeText.text.toString()
                    .isEmpty() -> {
                Toast.makeText(requireContext(), "Ubicacion GPS Requerida", Toast.LENGTH_SHORT)
                        .show()
                false
            }
            binding.sectionAutoComplete.text.toString().isEmpty() -> {
                binding.sectionAutoComplete.error = "Requerido"
                false
            }
            else -> true
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
                    Toast.makeText(requireContext(), ex.message.toString(), Toast.LENGTH_SHORT).show()
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    photoUri = FileProvider.getUriForFile(
                            requireContext(),
                            "com.example.android.fileprovider",
                            it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            photoBitmap = BitmapFactory.decodeFile(photoFile?.absolutePath)
//            photoBitmap = data?.extras?.get("data") as Bitmap
            val byteArrayOutputStream = ByteArrayOutputStream()
            photoBitmap?.let {
                it.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
                photoEncoded = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)
            }
            Glide.with(requireContext()).load(photoUri).into(binding.pictureImageView)
        }

    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
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