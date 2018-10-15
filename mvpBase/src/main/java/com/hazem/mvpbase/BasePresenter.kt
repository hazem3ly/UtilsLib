package com.hazem.mvpbase

import android.content.Context
import android.content.Intent
import android.os.Bundle


abstract class BasePresenter<P : BaseView>  constructor(val view: P) {
    var context: Context = view.getCurrentContext()
    abstract fun resume()
    abstract fun pause()
    abstract fun create(savedInstance: Bundle?)
    abstract fun destroy()
    abstract fun start()
    abstract fun stop()
    abstract fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
}