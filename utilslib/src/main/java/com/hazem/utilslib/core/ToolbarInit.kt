package com.hazem.utilslib.core

interface ToolbarInit {
    fun getToolbarId(): Int
    fun initToolbarTitle(): String
    fun getToolbarTextColor(): Int
    fun onToolbarClicked()
    fun showHomeUpButton(): Boolean
}