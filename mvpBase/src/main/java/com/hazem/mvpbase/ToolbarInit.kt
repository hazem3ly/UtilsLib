package com.hazem.mvpbase

interface ToolbarInit {
    fun getToolbarId(): Int
    fun initToolbarTitle(): String
    fun getToolbarTextColor(): Int
    fun onToolbarClicked()
    fun showHomeUpButton(): Boolean
}