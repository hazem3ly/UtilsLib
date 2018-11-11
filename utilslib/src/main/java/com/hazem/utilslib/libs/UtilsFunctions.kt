package com.hazem.utilslib.libs

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.util.Base64
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import java.io.ByteArrayOutputStream
import java.util.*


object UtilsFunctions {
    fun validateETNotEmpty(editText: EditText, errorMsg: String): Boolean {
        return if (editText.text.toString().isEmpty()) {
            editText.error = errorMsg
            false
        } else
            true
    }

    fun isValidEmail(editText: EditText, errorMsg: String): Boolean {
        return if (!editText.text.toString().isEmpty()
                && android.util.Patterns.EMAIL_ADDRESS.matcher(editText.text.toString()).matches())
            true
        else {
            editText.error = errorMsg
            false
        }
    }

    fun getSoftButtonsBarSizePort(activity: Activity): Int {
        val metrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metrics)
        val usableHeight = metrics.heightPixels
        activity.windowManager.defaultDisplay.getRealMetrics(metrics)
        val realHeight = metrics.heightPixels
        return if (realHeight > usableHeight) realHeight - usableHeight
        else 0
    }

    fun setTaskBarColored(context: Activity, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w = context.window
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            //status bar height
            val statusBarHeight = getStatusBarHeight(context)

            val view = View(context)
            view.layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            view.layoutParams.height = statusBarHeight
            (w.decorView as ViewGroup).addView(view)
            view.setBackgroundColor(context.resources.getColor(color))
        }
    }


    fun getStatusBarHeight(context: Activity): Int {
        var result = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    fun centerToolbarTitle(toolbar: Toolbar) {
        val title = toolbar.title
        val outViews = ArrayList<View>(1)
        toolbar.findViewsWithText(outViews, title, View.FIND_VIEWS_WITH_TEXT)
        if (!outViews.isEmpty()) {
            val titleView = outViews[0] as TextView
            titleView.gravity = Gravity.CENTER
            val layoutParams = titleView.layoutParams as Toolbar.LayoutParams
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            toolbar.requestLayout()
        }
    }

//    fun showErrorDialog(context: Context, msg: String, onClick: () -> Unit, onDismiss: () -> Unit) {
//        val dialog = AlertDialog.Builder(context)
//        val view = LayoutInflater.from(context).inflate(R.layout.error_dialog_message, null)
//
//        val message = view.findViewById<TextView>(R.id.message)
//        if (!msg.isEmpty())
//            message.text = msg
//        val button = view.findViewById<CardView>(R.id.button)
//
////        val closeBtn = view.findViewById<ImageButton>(R.id.close_btn)
//        dialog.setView(view)
//        val d = dialog.create()
//        d?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        button.setOnClickListener {
//            onClick()
//            d.dismiss()
//        }
////        closeBtn.setOnClickListener {
////            d.dismiss()
////        }
//
//        d.setOnDismissListener { onDismiss() }
//        d.show()
//
//    }
//
//    fun showSuccessDialog(context: Context, msg: String, onClick: () -> Unit) {
//        val dialog = AlertDialog.Builder(context)
//        val view = LayoutInflater.from(context).inflate(R.layout.dialog_message, null)
//
//        dialog.setView(view)
//
//        val message = view.findViewById<TextView>(R.id.message)
//        if (!msg.isEmpty())
//            message.text = msg
//        val button = view.findViewById<CardView>(R.id.button)
//
//        val d = dialog.create()
//        d?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        button.setOnClickListener {
//            onClick()
//            d.dismiss()
//        }
//        d.show()
//    }

    fun getScreenWidth(activity: Activity): Int {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        return displayMetrics.widthPixels
    }

    fun getScreenHight(activity: Activity): Int {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    fun getBase64FromFile(path: String): String? {

        var bmp: Bitmap? = null
        var baos: ByteArrayOutputStream? = null
        var baat: ByteArray? = null
        var encodeString: String? = null
        try {
            bmp = BitmapFactory.decodeFile(path)
            baos = ByteArrayOutputStream()
            bmp!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            baat = baos.toByteArray()
            encodeString = Base64.encodeToString(baat, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return encodeString
    }

    val PREFS_NAME = "utils_lib_data"
    val PREF_LANGUAGE = "language"



    fun showLanguageDialog(context: Context) {
        // setup the alert builder
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Choose App Language")
        val arrayAdapter = ArrayAdapter<String>(context, android.R.layout.select_dialog_singlechoice)
        arrayAdapter.add("ENGLISH")
        arrayAdapter.add("العربية")


        builder.setAdapter(arrayAdapter) { _, which ->
            changeLanguage(context, which)
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun changeLanguage(context: Context, which: Int) {
        when (which) {
            0 -> setAppLanguage(context, "en")
            1 -> setAppLanguage(context, "ar")
        }
    }

    fun setAppLanguage(context: Context, languageCode: String) {
        try {
            val locale = Locale(languageCode)
            val lang = locale.language
            val current = context.resources.configuration.locale
            val LAng = current.language
            if (lang != LAng) {
                Locale.setDefault(locale)
                val config = Configuration()
                config.locale = locale
                context.resources.updateConfiguration(config, context.resources.displayMetrics)

                saveLanguage(context, languageCode)
                if (context is Activity) {
//                    ((HomeActivity)(context)).recreate();


                    val i = context.baseContext.packageManager
                            .getLaunchIntentForPackage(context.baseContext.packageName)
                    i?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    context.startActivity(i)
                    context.overridePendingTransition(0, 0)
                    /* val i = context.intent
                     context.startActivity(i)
                     context.overridePendingTransition(0, 0)
                     context.finish()*/
//                    context.recreate()
                }

            }
        } catch (e: Exception) {

        }

    }

    private fun saveLanguage(context: Context, languageCode: String) {
        val editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        editor.putString(PREF_LANGUAGE, languageCode)
        editor.apply()
    }

    fun getCurrentLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(PREF_LANGUAGE, "en") ?: "en"
    }

}