package com.capstoneproject

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.capstoneproject.model.History
import com.capstoneproject.model.User
import com.capstoneproject.ui.*
import com.capstoneproject.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class Firestore {
    private val fstore = FirebaseFirestore.getInstance()

    fun getCurrentUser(): String{
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""

        if (currentUser != null){
            currentUserId = currentUser.uid
        }
        return currentUserId
    }

    fun getUserDetail(activity:Activity){
        fstore.collection(Constants.USERS)
                .document(getCurrentUser())
                .get()
                .addOnSuccessListener {
                    Log.i(activity.javaClass.simpleName, it.toString())

                    val user = it.toObject(User::class.java)!!
                    val sharedPreferences = activity.getSharedPreferences(
                            Constants.NAME_PREFERENCES,
                            Context.MODE_PRIVATE
                    )

                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    editor.putString(
                            Constants.LOGGED_USERNAME,
                        user.fullName
                    )

                    editor.apply()

                    val email_data: SharedPreferences.Editor = sharedPreferences.edit()
                    email_data.putString(
                        Constants.EMAIL_DATA,
                        user.email
                    )
                    email_data.apply()

                    val nik_data: SharedPreferences.Editor = sharedPreferences.edit()
                    nik_data.putString(
                        Constants.NIK,
                        user.noKTP
                    )
                    nik_data.apply()

                    when (activity){
                        is LoginActivity ->{
                            activity.userLoginSucsess(user)
                        }
                        is RegisterActivity-> {
                            activity.userRegisterSucsess(user)
                        }
                    }
                }
                .addOnFailureListener {
                    Log.e(activity.javaClass.simpleName,
                    "Error while register user")
                }
    }

    fun uploadImagetoCloudStorage(activity: Activity, imageFileURI: Uri?){
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            Constants.IMAGE + System.currentTimeMillis()+"."
        +Constants.getFileEXtension(
                activity,
                imageFileURI
        )
        )
        sRef.putFile(imageFileURI!!).addOnSuccessListener {
            Log.e(
                "Firebase Image URL",
                it.metadata!!.reference!!.downloadUrl.toString()
            )
            it.metadata!!.reference!!.downloadUrl
                .addOnSuccessListener {
                    Log.e("Downloadable image URL", it.toString())
                    when (activity){
                        is UploadActivity ->{
                            activity.imageUploadSuccess(it.toString())
                        }
                    }
                }
        }
            .addOnFailureListener {
                when(activity){
                    is UploadActivity->{
                        Log.e(
                            activity.javaClass.simpleName,
                            it.message,
                            it
                        )
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    it.message,
                    it
                )
            }
    }

    fun addDiagnosisHistory(activity: Activity, model: String, imageURL: String , latitude : String , longitude : String) {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH)
        val date = dateFormat.format(Date())
        val newHistoryData = HashMap<String, Any>()
        newHistoryData["image"] = imageURL
        newHistoryData["predictions"] = listOf(model)
        newHistoryData["status"] = "doing"
        newHistoryData["user_id"] = getCurrentUser()
        newHistoryData["request_date"] = date
        newHistoryData["longitude"] = longitude
        newHistoryData["latitude"] = latitude

        fstore.collection(Constants.HISTORY)
            .add(newHistoryData)
            .addOnSuccessListener {
                when(activity){
                    is UploadActivity ->{
                        activity.historyCreationSuccess(it.id)
                    }
                }
            }
            .addOnFailureListener {
                when(activity){
                    is UploadActivity->{
                        Log.e(
                            activity.javaClass.simpleName,
                            it.message,
                            it
                        )
                    }
                }
            }

    }

    fun getScreeningHistoryDetail(activity: Activity, historyId: String) {
        fstore.collection(Constants.HISTORY)
            .document(historyId)
            .get()
            .addOnSuccessListener {
                val data = it.data ?: mapOf()
                when(activity) {
                    is ResultActivity -> activity.onHistoryFetchSuccess(data)
                }
            }
            .addOnFailureListener {
                Log.e(
                    activity.javaClass.simpleName,
                    it.message,
                    it
                )
            }
    }

    fun getScreeningHistoryList(activity: Activity, type: String) {
        fstore.collection(Constants.HISTORY)
            .whereEqualTo("user_id", getCurrentUser())
            .get()
            .addOnSuccessListener {
                val result = arrayListOf<History>()
                it.documents.forEach { it1 ->
                    try {
                        val data = it1.data as Map<String, Any>
                        val chosenDiagnosis = (data["predictions"] as ArrayList<String>)[0]
                        val history = History(
                            id = it1.id,
                            user_id = data["user_id"] as String,
                            models = data["predictions"] as ArrayList<String>,
                            imageUrl = data["image"] as String,
                            longitude = data["longitude"] as String,
                            latitude = data["latitude"] as String,
                            request_date = data["request_date"] as String,
                            positivePercentage = ((
                                    data["result"] as HashMap<String, Any>
                                    )[chosenDiagnosis] as HashMap<String, Any>
                                    )[chosenDiagnosis].toString().toDouble() * 100,
                            negativePercentage = ((
                                    data["result"] as HashMap<String, Any>
                                    )[chosenDiagnosis] as HashMap<String, Any>
                                    )["normal"].toString().toDouble() * 100
                        )
                        if(type != "null"){
                            if(type == "covid"){
                                if(history.models[0] == "covid"){
                                    result.add(history);
                                }
                            }
                            if(type == "pneumonia"){
                                if(history.models[0] == "pneumonia"){
                                    result.add(history);
                                }
                            }
                        }else{
                            result.add(history);
                        }
                    }
                    catch(e: Exception) { }
                }
                when(activity) {
                    is HistoryActivity -> activity.onListFetchSuccess(result)
                }
            }
            .addOnFailureListener {
                Log.e(
                    activity.javaClass.simpleName,
                    it.message,
                    it
                )
                when(activity) {
                    is HistoryActivity -> activity.onListFetchSuccess(arrayListOf())
                }
            }
    }

}