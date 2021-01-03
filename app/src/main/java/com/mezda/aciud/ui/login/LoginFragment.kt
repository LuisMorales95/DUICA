package com.mezda.aciud.ui.login

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.mezda.aciud.R
import com.mezda.aciud.databinding.FragmentLoginBinding
import com.mezda.aciud.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment() {

    private lateinit var binding: FragmentLoginBinding
    private val loginViewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        binding.registerText.setOnClickListener {
            launchDirection(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }

        binding.userTextInputLayout.editText?.setText("Admin")

        binding.startButton.setOnClickListener {
            val user = binding.userTextInputLayout.editText?.text?.toString() ?: ""
            if (user.isEmpty()) {
                binding.userTextInputLayout.error = "Usuario Requerido"
            } else {
                binding.userTextInputLayout.isErrorEnabled = false
                loginViewModel.login(user)
            }
        }

        loginViewModel.messages.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                loginViewModel.messageShown()
            }
        })

        loginViewModel.loading.observe(viewLifecycleOwner, {
            binding.startButton.visibility = if (it.get() == true) View.GONE else View.VISIBLE
        })

        loginViewModel.loginSuccess.observe(viewLifecycleOwner, {
            if (it.get() == true) {
                launchDirection(LoginFragmentDirections.actionLoginFragmentToSearchFragment())
                loginViewModel.loggedSuccessful()
            }
        })
        validatePermissions()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(requireView()).load(R.drawable.aciud).into(binding.logoImage)
        Glide.with(requireView()).load(R.drawable.morena_logo).into(binding.imageView)
    }

    companion object {
        const val REQUEST_MULTIPLE_PERMISSIONS = 101
    }

    private fun validatePermissions() {
        if (hasPhonePermission()) {
            requestPhonePermission()
        }
    }

    private fun requestPhonePermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ), REQUEST_MULTIPLE_PERMISSIONS
        )
    }

    private fun hasPhonePermission() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_MULTIPLE_PERMISSIONS -> {
                if (grantResults.isEmpty() || grantResults.any { permission -> permission != PackageManager.PERMISSION_GRANTED }) {
                    Toast.makeText(
                        requireContext(),
                        "Debe acceptar los permisos",
                        Toast.LENGTH_SHORT
                    ).show()
                    requestPhonePermission()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Permisos Habilitados",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }
}