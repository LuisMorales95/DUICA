package com.mezda.aciud.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.mezda.aciud.R
import com.mezda.aciud.databinding.FragmentLoginBinding
import com.mezda.aciud.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment() {

    private lateinit var binding: FragmentLoginBinding
    private val loginViewModel by viewModels<LoginViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.registerText.setOnClickListener {
            launchDirection(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }
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

        binding.userTextInputLayout.editText?.setText("Chelis")
        return binding.root
    }


}