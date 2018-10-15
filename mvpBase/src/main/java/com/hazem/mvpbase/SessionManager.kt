package com.hazem.mvpbase

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences


class SessionManager(val context: Context) {
    // User name (make variable public to access from outside)

    private val ID: String = "id"
    private val NAME: String = "name"
    private val EMAIL: String = "email"
    private val LEVEL: String = "level"
    private val STATUS: String = "status"
    private val CREATED_AT: String = "created_at"
    private val UPDATED_AT: String = "updated_at"
    private val TOKEN: String = "token"


    // Sharedpref file name
    private val PREF_NAME = "LoggingSession"
    // All Shared Preferences Keys
    private val IS_LOGIN = "IsLoggedIn"
    // Shared Preferences
    var pref: SharedPreferences
    // Editor for Shared preferences
    var editor: SharedPreferences.Editor
    // Context
    // Shared pref mode
    var PRIVATE_MODE = 0

    init {
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
    }

    /**
     * Create login session
     */
//    fun createLoginSession(userResponse: UserResponse) {
//        // Storing login value as TRUE
//        editor.putBoolean(IS_LOGIN, true)
//
//
//        editor.putInt(ID, userResponse.user?.id ?: -1)
//        editor.putString(NAME, userResponse.user?.name)
//        editor.putString(EMAIL, userResponse.user?.email)
//        editor.putString(LEVEL, userResponse.user?.level)
//        editor.putString(STATUS, userResponse.user?.status)
//        editor.putString(CREATED_AT, userResponse.user?.createdAt)
//        editor.putString(UPDATED_AT, userResponse.user?.updatedAt)
//        editor.putString(TOKEN, userResponse.token)
//
//
//        // commit changes
//        editor.commit()
//    }

    /**
     * Get stored session data
     */
//    fun getUserDetails(): UserResponse {
//
//        val userResponse = UserResponse()
//
//        val user = User()
//        user.id = pref.getInt(ID, -1)
//        user.name = pref.getString(NAME, "")
//        user.email = pref.getString(EMAIL, "")
//        user.level = pref.getString(STATUS, "")
//        user.status = pref.getString(STATUS, "")
//        user.createdAt = pref.getString(CREATED_AT, "")
//        user.updatedAt = pref.getString(UPDATED_AT, "")
//
//        userResponse.user = user
//        userResponse.level = pref.getString(LEVEL, "")
//        userResponse.token = pref.getString(TOKEN, "")
//
//        return userResponse
//    }

    /**
     * Clear session details
     */
//    fun logoutUser() {
//        // Clearing all data from Shared Preferences
//        editor.clear()
//        editor.commit()
//
//        // After logout redirect user to Loing Activity
//        val i = Intent(context, LoginActivity::class.java)
//        // Closing all the Activities
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//
//        // Add new Flag to start new Activity
//        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//
//        // Staring Login Activity
//        context.startActivity(i)
//    }

    /**
     * Quick check for login
     */
    // Get Login State
    fun isLoggedIn(): Boolean {
        return pref.getBoolean(IS_LOGIN, false)
    }
}