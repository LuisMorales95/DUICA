package com.mezda.aciud

import android.app.Activity
import android.app.Application
import android.content.Context
import android.text.InputFilter
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import com.mezda.aciud.utils.SuburbFocusListener
import dagger.hilt.android.HiltAndroidApp
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import timber.log.Timber
import timber.log.Timber.DebugTree

fun hideSoftKeyBoard(context: Activity) {
    try {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun <T> Fragment.showAutoCompleteOptions(
    autoCompleteTextView: AutoCompleteTextView,
    adapter: ArrayAdapter<T>,
    filter: Array<InputFilter>? = null,
    validator: AutoCompleteTextView.Validator? = null,
    focusListener: View.OnFocusChangeListener? = null
) {
    autoCompleteTextView.threshold = 0
    autoCompleteTextView.setOnFocusChangeListener { _, hasFocus ->
        if (hasFocus) {
            autoCompleteTextView.showDropDown()
            hideSoftKeyBoard(requireActivity())
        }
    }
    autoCompleteTextView.setOnClickListener {
        autoCompleteTextView.showDropDown()
        hideSoftKeyBoard(requireActivity())
    }
    autoCompleteTextView.setAdapter(adapter)
    autoCompleteTextView.showSoftInputOnFocus = false
    filter?.let { autoCompleteTextView.filters = it }
    validator?.let { autoCompleteTextView.validator = it }
    focusListener?.let { autoCompleteTextView.onFocusChangeListener = focusListener }
}

@HiltAndroidApp
class ACIUDApp : Application() {

    companion object {

        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()

    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }
    }

    private class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return
            }
//            FakeCrashLibrary.log(priority, tag, message)
            if (t != null) {
                if (priority == Log.ERROR) {
//                    FakeCrashLibrary.logError(t)
                } else if (priority == Log.WARN) {
//                    FakeCrashLibrary.logWarning(t)
                }
            }
        }
    }
}