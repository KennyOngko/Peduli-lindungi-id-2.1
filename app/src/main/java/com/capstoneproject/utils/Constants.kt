package com.capstoneproject.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {
    const val USERS: String = "users"
    const val NAME_PREFERENCES: String= "PeduliLindungi"
    const val LOGGED_USERNAME: String = "Logged_Username"
    const val EMAIL_DATA: String= "email_data"
    const val READ_PERMISSION = 2
    const val PICK_IMAGE = 1
    const val IMAGE: String = "image"
    const val HISTORY: String = "history"

    const val NIK: String = "nik"

    const val COVID_MODEL = "covid"
    const val PNEUMONIA_MODEL = "pneumonia"

    const val API_URL = "https://bias-server-aa2xij5ltq-et.a.run.app/"


    fun showImageChooser(activity:Activity){
        val galeryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        activity.startActivityForResult(galeryIntent, PICK_IMAGE)
    }

    fun getFileEXtension(activity: Activity, uri: Uri?): String?{

        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))

    }
}