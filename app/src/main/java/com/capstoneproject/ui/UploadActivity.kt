package com.capstoneproject.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.capstoneproject.Firestore
import com.capstoneproject.R
import com.capstoneproject.api.PredictorApi
import com.capstoneproject.model.PredictionRequest
import com.capstoneproject.utils.Constants
import com.capstoneproject.utils.GlideLoader
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_upload.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


class UploadActivity : AppCompatActivity(),View.OnClickListener {

    companion object {
        const val EXTRA_MODEL_TYPE = "EXTRA_MODEL_TYPE"
    }

    private lateinit var progressBar: ProgressBar

    private var mSelectedModel: String = ""
    private var mSelectedImage: Uri? = null
    private var mImageURL: String = ""
    lateinit var client : FusedLocationProviderClient
    var longitude : String = ""
    var latitude : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        mSelectedModel = intent.getStringExtra(EXTRA_MODEL_TYPE) ?: ""

        val fullname_upload: TextView = findViewById(R.id.fullname_upload)
        val email_upload: TextView = findViewById(R.id.email_upload)
        val btn_add_image_upload: Button = findViewById(R.id.btn_add_image_upload)
        val btn_diagnose: Button = findViewById(R.id.btn_diagnose)
        progressBar = findViewById(R.id.progresbar)
        client = LocationServices.getFusedLocationProviderClient(getBaseContext());


        val sharedPreferences = getSharedPreferences(Constants.NAME_PREFERENCES, Context.MODE_PRIVATE)
        val username = sharedPreferences.getString(Constants.LOGGED_USERNAME, "")!!
        fullname_upload.text = username

        val email = sharedPreferences.getString(Constants.EMAIL_DATA, "")!!
        email_upload.text = email




        btn_add_image_upload.setOnClickListener(this@UploadActivity)
        btn_diagnose.setOnClickListener(this@UploadActivity)

        getLocation();
    }

    fun getLocation () {
        if(checkPermission()) {
            //permission grantet
            val task: Task<Location> = client.lastLocation
            task.addOnSuccessListener(object : OnSuccessListener<Location?> {
                override fun onSuccess(location: Location?) {
                    if (location != null) {
                        longitude = location.longitude.toString()
                        latitude = location.latitude.toString()
                    }
                }
            })
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),44);
        }
    }

    fun checkPermission() : Boolean {
        //boolean
        return ActivityCompat.checkSelfPermission(getBaseContext() , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    override fun onClick(v: View?) {
        if(v != null){
            when(v.id){
                R.id.btn_add_image_upload ->{
                    if(ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ){
                        Constants.showImageChooser(this)
                        Toast.makeText(this,"You already have the storage permission.", Toast.LENGTH_SHORT).show()
                    } else{

                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_PERMISSION
                        )
                    }
                }

                R.id.btn_diagnose ->{
                    progressBar.visibility = View.VISIBLE
                    if (mSelectedImage != null){
                        Firestore().uploadImagetoCloudStorage(this, mSelectedImage)
                        Toast.makeText(this,"Image Uploaded.", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        updateImage()
                    }
                }
            }
        }
    }
    private fun updateImage(){
        if (mImageURL.isNotEmpty()) {
            Firestore().addDiagnosisHistory(this, mSelectedModel, mImageURL , longitude , latitude)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == Constants.READ_PERMISSION){
            if (grantResults.isNotEmpty()&& grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)

            }else{
                Toast.makeText(this,"read storage permission is denied.", Toast.LENGTH_SHORT).show()

            }
        }
        if(requestCode == 44){
            if (grantResults.isNotEmpty()&& grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLocation();
            }
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == Constants.PICK_IMAGE){
                if (data != null){
                    try {
                        mSelectedImage = data.data!!

                        GlideLoader(this).loadImage(mSelectedImage!!,iv_xray_upload)
                    }catch (e: IOException){
                        e.printStackTrace()
                        Toast.makeText(this,"Image selection failed.", Toast.LENGTH_SHORT).show()

                    }
                }
            }
        }
    }

    fun imageUploadSuccess(imageURL: String){

        Toast.makeText(this@UploadActivity,"Upload Image successfully. Image URL is $imageURL", Toast.LENGTH_SHORT).show()

        mImageURL = imageURL
        updateImage()
    }

    fun historyCreationSuccess(historyId: String) {
        val request = PredictionRequest(historyId)
        PredictorApi.client.requestPrediction(request).enqueue(object:
            Callback<HashMap<String, Boolean>> {
            override fun onResponse(
                call: Call<HashMap<String, Boolean>>,
                response: Response<HashMap<String, Boolean>>
            ) {
                if(response.isSuccessful) {
                    Toast.makeText(this@UploadActivity,"Prediction Success, Redirecting", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                    val intent = Intent(this@UploadActivity, ResultActivity::class.java)
                    intent.putExtra(ResultActivity.EXTRA_HISTORY_ID, historyId)
                    startActivity(intent)
                }
                else {
                    Log.e(this@UploadActivity.toString(), response.errorBody().toString())
                    Toast.makeText(this@UploadActivity,"Fail to request prediction", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<HashMap<String, Boolean>>, t: Throwable) {
                Log.e(this@UploadActivity.toString(), t.toString())
                Toast.makeText(this@UploadActivity,"Fail to request prediction", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            }

        }
        )
    }


}

