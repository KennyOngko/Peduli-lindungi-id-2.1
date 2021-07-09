package com.capstoneproject.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(
    val id: String = "",
    val fullName: String = "",
    val email:String = "",

    val address:String = "",
    val phone:String = "",
    val noKTP:String = "",

    val image:String = ""
):Parcelable