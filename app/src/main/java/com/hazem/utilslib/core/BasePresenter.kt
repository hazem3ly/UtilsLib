package com.hazem.utilslib.core

import android.content.Context
import android.content.Intent
import android.os.Bundle


abstract class BasePresenter<P : BaseView> internal constructor(val view: P) {
    var context: Context = view.getCurrentContext()
    abstract fun resume()
    abstract fun pause()
    abstract fun create(savedInstance: Bundle?)
    abstract fun destroy()
    abstract fun start()
    abstract fun stop()
    abstract fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)

  /*  fun validateETNotEmpty(editText: EditText): Boolean {
        return if (editText.text.toString().isEmpty()) {
            editText.error = context.getString(R.string.emptyEditText)
            false
        } else
            true
    }

    fun isValidEmail(editText: EditText): Boolean {
        return if (!editText.text.toString().isEmpty()
                && android.util.Patterns.EMAIL_ADDRESS.matcher(editText.text.toString()).matches())
            true
        else {
            editText.error = context.getString(R.string.invalid_email)
            false
        }
    }*/

}