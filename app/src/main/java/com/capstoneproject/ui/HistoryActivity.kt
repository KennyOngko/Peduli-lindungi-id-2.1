package com.capstoneproject.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstoneproject.Firestore
import com.capstoneproject.databinding.ActivityHistoryBinding
import com.capstoneproject.model.History
import com.capstoneproject.ui.adapter.HistoryViewAdapter
import com.capstoneproject.utils.Constants

class HistoryActivity : AppCompatActivity() {

    private lateinit var activityBinding: ActivityHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityHistoryBinding.inflate(layoutInflater)

        val sharedPreferences = getSharedPreferences(
            Constants.NAME_PREFERENCES,
            Context.MODE_PRIVATE
        )
        activityBinding.username.text =
            sharedPreferences.getString(Constants.LOGGED_USERNAME, "").toString()

        Firestore().getScreeningHistoryList(this)

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