package com.mezda.aciud.ui.login

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.mezda.aciud.R

class LoginFragmentDirections private constructor() {
  companion object {
    fun actionLoginFragmentToRegisterFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_loginFragment_to_registerFragment)

    fun actionLoginFragmentToSearchFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_loginFragment_to_searchFragment)
  }
}
