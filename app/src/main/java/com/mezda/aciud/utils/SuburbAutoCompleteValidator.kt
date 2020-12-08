package com.mezda.aciud.utils

import android.widget.AutoCompleteTextView
import timber.log.Timber

class SuburbAutoCompleteValidator (val list: MutableList<String>): AutoCompleteTextView.Validator {
    override fun isValid(p0: CharSequence?): Boolean {
        Timber.e("Text to validate ${p0.toString()}")
        var found = false
        run loop@ {
            list.forEach {
                if (it == p0.toString()){
                    found = true
                    return@loop
                }
            }
        }
        return found
//        Arrays.sort(list.toTypedArray())
//        if(Arrays.binarySearch(list.toTypedArray(),p0.toString()) > 0) {
//            return true
//        }
//        return false
    }

    override fun fixText(p0: CharSequence?): CharSequence {
        Timber.e("Returning fixed text")
        return ""
    }
}