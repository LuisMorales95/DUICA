package com.mezda.aciud.ui.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mezda.aciud.R
import com.mezda.aciud.databinding.FragmentRegisterBinding
import com.mezda.aciud.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class RegisterFragment : BaseFragment() {

    private lateinit var binding: FragmentRegisterBinding

    private val registerViewModel by viewModels<RegisterViewModel>()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        registerViewModel.onStart()

        registerViewModel.messages.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                registerViewModel.messageShown()
            }
        })

        registerViewModel.registerSuccess.observe(viewLifecycleOwner, {
            if (it.get() == true) {
                popBack()
                registerViewModel.registerSuccessful()
            }
        })

        registerViewModel.supervisor.observe(viewLifecycleOwner, {
            binding.supervisorSpinner.adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    it.toTypedArray()
            )
        })

        registerViewModel.loading.observe(viewLifecycleOwner, {
            binding.registerButton.visibility = if (it.get() == true) View.GONE else View.VISIBLE
        })

        binding.supervisorSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {

                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        Timber.e("$p2 $p3")
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {}
                }

        binding.registerButton.setOnClickListener {
            val user = binding.userTextInputLayout.editText?.text.toString()
            val name = binding.nameTextInputLayout.editText?.text.toString()
            val password = binding.passwordTextInputLayout.editText?.text.toString()
            val supervisorId = binding.supervisorSpinner.selectedItemPosition
            if (validateFormNotEmpty()) {
                registerViewModel.onRegisterUser(user, password, name, supervisorId)
            }
        }
        return binding.root
    }

    private fun validateFormNotEmpty(): Boolean {
        binding.userTextInputLayout.isErrorEnabled = false
        binding.nameTextInputLayout.isErrorEnabled = false
        return when {
            binding.userTextInputLayout.editText?.text.toString().isEmpty() -> {
                binding.userTextInputLayout.error = "Requerido"
                false
            }
            binding.passwordTextInputLayout.editText?.text.toString().isEmpty() -> {
                binding.passwordTextInputLayout.error = "Requerido"
                false
            }
            binding.nameTextInputLayout.editText?.text.toString().isEmpty() -> {
                binding.nameTextInputLayout.error = "Requerido"
                false
            }
            binding.supervisorSpinner.selectedItemPosition == 0 -> {
                Toast.makeText(requireContext(), "Selecciona un responsable", Toast.LENGTH_SHORT)
                        .show()
                false
            }
            else -> true
        }
    }
}