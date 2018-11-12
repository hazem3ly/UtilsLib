package com.hazem.sample.testActivity

import android.content.Intent
import android.os.Bundle
import com.hazem.mvpbase.BasePresenter

class TestPresenter(view: TestView) : BasePresenter<TestView>(view) {
    override fun resume() {

    }

    override fun pause() {
    }

    override fun create(savedInstance: Bundle?) {
    }

    override fun destroy() {
    }

    override fun start() {
    }

    override fun stop() {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    }
}