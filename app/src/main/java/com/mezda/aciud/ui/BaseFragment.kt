package com.mezda.aciud.ui

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import timber.log.Timber

abstract class BaseFragment: Fragment() {

    @Throws
    fun launchDirection(navDirections: NavDirections) {
        try {
            Navigation.findNavController(requireView()).navigate(navDirections)
//            findNavController().navigate(navDirections)
        } catch(exception: Exception) {
            Timber.e(exception)
            /*uiScope.launch {
                withContext(Dispatchers.IO) {
                    FirebaseCrashlytics.getInstance().apply {
                        recordException(exception)
                        sendUnsentReports()
                    }
                }
            }*/
        }
    }

    fun popBack() {
        Navigation.findNavController(requireView()).popBackStack()
    }

    fun toast(msg:String){
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
}