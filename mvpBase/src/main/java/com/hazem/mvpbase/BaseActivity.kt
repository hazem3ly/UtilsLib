package com.hazem.mvpbase

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import java.util.*

abstract class BaseActivity<T : BasePresenter<*>> : AppCompatActivity(), BaseView {

    private var toolbar: Toolbar? = null
    lateinit var presenter: T

    fun isRTL(): Boolean {
        return isRTL(Locale.getDefault())
    }

    fun isRTL(locale: Locale): Boolean {
        val directionality = Character.getDirectionality(locale.displayName.toString()[0]).toInt()
        return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT.toInt()
                || directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC.toInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // load custom resource layout

//        window.decorView.layoutDirection =
//                if (isRTL()) View.LAYOUT_DIRECTION_RTL else View.LAYOUT_DIRECTION_LTR

        setContentView(loadResourceLayout())

        presenter = initPresenter()
        presenter.context = this
        presenter.create(savedInstanceState)


        // check if this activity has toolbar implementation
        if (this is ToolbarInit) {
            val toolbarImpl: ToolbarInit? = this
            toolbar = toolbarImpl?.getToolbarId()?.let { findViewById(it) }
            // set this toolbar as actionbar
            setSupportActionBar(toolbar)
            // set toolbar color
            toolbarImpl?.getToolbarTextColor()?.let {
                ContextCompat.getColor(this, it)
            }?.let {
                toolbar?.setTitleTextColor(it)
            }
            // forward navigation clicked to implemented activity
            toolbar?.setNavigationOnClickListener { toolbarImpl?.onToolbarClicked() }

            val actionBar: ActionBar? = supportActionBar
            // set actionbar title
            actionBar?.title = toolbarImpl?.initToolbarTitle()
            actionBar?.setDisplayHomeAsUpEnabled(toolbarImpl!!.showHomeUpButton())
            actionBar?.setDisplayShowHomeEnabled(toolbarImpl!!.showHomeUpButton())
        }
        initViews()

        val connectionLiveData = ConnectionLiveData(this)
        connectionLiveData.observe(this, Observer { isConnected ->
            onNetworkChanged(isConnected)
        })
    }

    abstract fun onNetworkChanged(isConnected: Boolean?)

    override fun attachBaseContext(base: Context?) {
        if (base == null) super.attachBaseContext(this)
        else {
            val lang = getSavedLanguage(base)
            super.attachBaseContext(MyContextWrapper.wrap(base, lang))
        }
    }

    fun getSavedLanguage(context: Context): String {
        return MyContextWrapper.getCurrentLanguage(context)
    }

    abstract fun loadResourceLayout(): Int
    abstract fun initPresenter(): T
    abstract fun initViews()

    abstract fun onMenuItemClicked(itemId: Int)

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (this is MenuInit) {
            val menuInit = this as MenuInit
            val inflater = menuInflater
            inflater.inflate(menuInit.initMenuRes(), menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onMenuItemClicked(item.itemId)
        return true
    }

    override fun getCurrentContext() = this
    override fun getCurrentActivity() = this

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStart() {
        super.onStart()
        presenter.start()
    }

    override fun onStop() {
        super.onStop()
        presenter.stop()
    }

    override fun onResume() {
        super.onResume()
        presenter.resume()
    }

    override fun onPause() {
        super.onPause()
        presenter.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.destroy()
    }

}