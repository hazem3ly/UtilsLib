package com.hazem.mvpbase

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class BaseFragment<P : BasePresenter<*>> : Fragment(), LoadingView {

    var toolbar: Toolbar? = null
    lateinit var presenter: P
    var rootView: View? = null
    var isViewPrepared: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = initPresenter()
        presenter.context = getCurrentContext()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(setResourceLayout(), container, false)
        return rootView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.create(savedInstanceState)
        initViews()
        isViewPrepared = true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // check if this activity has toolbar implementation
        if (this is ToolbarInit) {
            val toolbarImpl: ToolbarInit? = this
            toolbar = toolbarImpl?.getToolbarId()?.let { rootView?.findViewById(it) }
            // set this toolbar as actionbar
            if (activity is AppCompatActivity)
                (activity as AppCompatActivity).setSupportActionBar(toolbar)
            // set toolbar color
            toolbarImpl?.getToolbarTextColor()?.let {
                ContextCompat.getColor(getCurrentContext(), it)
            }?.let {
                toolbar?.setTitleTextColor(it)
            }
            // forward navigation clicked to implemented activity
            toolbar?.setNavigationOnClickListener {toolbarImpl?.onToolbarClicked() }

            if (activity is AppCompatActivity) {
                val actionBar: ActionBar? = (activity as AppCompatActivity).supportActionBar
                // set actionbar title
                actionBar?.title = toolbarImpl?.initToolbarTitle()
                actionBar?.setDisplayHomeAsUpEnabled(toolbarImpl!!.showHomeUpButton())
                actionBar?.setDisplayShowHomeEnabled(toolbarImpl!!.showHomeUpButton())
            }
        }
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


    abstract fun setResourceLayout(): Int
    abstract fun initPresenter(): P
    abstract fun initViews()


}