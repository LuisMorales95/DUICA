package com.mezda.aciud.utils

import android.view.View
import android.widget.AutoCompleteTextView
import com.mezda.aciud.R
import timber.log.Timber

class SuburbFocusListener: View.OnFocusChangeListener {
    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        Timber.e("Focus changed Test")
        if (view?.id == R.id.suburb_auto_complete && !hasFocus) {
            (view as AutoCompleteTextView).performValidation()
        }
    }
}