package com.capstoneproject.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstoneproject.Firestore
import com.capstoneproject.R
import com.capstoneproject.databinding.ActivityHistoryBinding
import com.capstoneproject.model.History
import com.capstoneproject.ui.adapter.HistoryViewAdapter
import com.capstoneproject.utils.Constants
import java.util.*


class HistoryActivity : AppCompatActivity() {

    private lateinit var activityBinding: ActivityHistoryBinding
    private var type: String = "covid"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        activityBinding = ActivityHistoryBinding.inflate(layoutInflater)
        activityBinding.ktlBtn.setOnClickListener{
            Firestore().getScreeningHistoryList(this, type);
            if(type == "pneumonia"){
                activityBinding.ktlBtn.setText("Covid")
                type = "covid"
            }else{
                activityBinding.ktlBtn.setText("Pneumonia")
                type = "pneumonia"
            }
        }

        val sharedPreferences = getSharedPreferences(
            Constants.NAME_PREFERENCES,
            Context.MODE_PRIVATE
        )
        activityBinding.username.text =
            sharedPreferences.getString(Constants.LOGGED_USERNAME, "").toString()

        Firestore().getScreeningHistoryList(this, type);

        setContentView(activityBinding.root)
    }

    fun onListFetchSuccess(data: ArrayList<History>) {
        val adapter = HistoryViewAdapter(data)
        activityBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        activityBinding.recyclerView.adapter = adapter

        activityBinding.progressBar.visibility = View.GONE
        activityBinding.recyclerView.visibility = View.VISIBLE

        adapter.setOnItemClickCallback(object: HistoryViewAdapter.OnItemClickCallback {
            override fun onItemClicked(data: History) {
                val intent = Intent(this@HistoryActivity, ResultActivity::class.java)
                intent.putExtra(ResultActivity.EXTRA_HISTORY_ID, data.id)
                startActivity(intent)
            }
        })
    }
}