package com.capstoneproject.ui

import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.capstoneproject.Firestore
import com.capstoneproject.databinding.ActivityResultBinding
import com.capstoneproject.utils.Constants
import com.capstoneproject.utils.GlideLoader

class ResultActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_HISTORY_ID = "EXTRA_HISTORY_ID"
    }

    private lateinit var activityBinding: ActivityResultBinding
    private lateinit var historyId: String
    private lateinit var userName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityResultBinding.inflate(layoutInflater)

        val sharedPreferences = getSharedPreferences(
            Constants.NAME_PREFERENCES,
            Context.MODE_PRIVATE
        )
        userName = sharedPreferences.getString(Constants.LOGGED_USERNAME, "").toString()

        historyId = intent.getStringExtra(EXTRA_HISTORY_ID) ?: ""
        Firestore().getScreeningHistoryDetail(this, historyId)

        setContentView(activityBinding.root)
    }

    fun onHistoryFetchSuccess(data: Map<String, Any>) {
        activityBinding.username.text = userName
        val chosenDiagnosis = (data["predictions"] as ArrayList<String>)[0]
        activityBinding.diagnosis.text = chosenDiagnosis

        val positiveResult = ((
                data["result"] as HashMap<String, Any>
                )[chosenDiagnosis] as HashMap<String, Any>
                )[chosenDiagnosis].toString().toDouble() * 100

        val negativeResult = ((
                data["result"] as HashMap<String, Any>
                )[chosenDiagnosis] as HashMap<String, Any>
                )["normal"].toString().toDouble() * 100

        val resultString = "Positive %.2f %%, Negative %.2f %%".format(positiveResult, negativeResult)
        activityBinding.result.text = resultString

        activityBinding.date.text = data["request_date"] as String? ?: ""

        GlideLoader(this).loadImage(Uri.parse(data["image"] as String), activityBinding.screeningImage)

    }
}