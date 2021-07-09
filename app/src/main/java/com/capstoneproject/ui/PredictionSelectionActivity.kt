package com.capstoneproject.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.capstoneproject.databinding.ActivityPredictionSelectionBinding
import com.capstoneproject.utils.Constants

class PredictionSelectionActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var activityBinding: ActivityPredictionSelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityPredictionSelectionBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)

        activityBinding.covidSelectionBtn.setOnClickListener(this)
        activityBinding.pneumoniaSelectionBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val chosenModel = when(v?.id) {
            activityBinding.covidSelectionBtn.id -> Constants.COVID_MODEL
            activityBinding.pneumoniaSelectionBtn.id -> Constants.PNEUMONIA_MODEL
            else -> ""
        }

        val intent = Intent(this@PredictionSelectionActivity, UploadActivity::class.java)
        intent.putExtra(UploadActivity.EXTRA_MODEL_TYPE, chosenModel)
        startActivity(intent)
    }
}
