package com.capstoneproject.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.capstoneproject.R
import java.io.IOException

class GlideLoader(val context: Context) {
    fun loadImage(imageURI: Uri, imageView: ImageView){
        try {
            Glide
                .with(context)
                .load(imageURI)
                .centerCrop()
                .placeholder(R.drawable.xray)
                .into(imageView)
        }catch (e: IOException){
            e.printStackTrace()
        }
    }
}