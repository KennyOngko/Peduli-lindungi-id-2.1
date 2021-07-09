package com.capstoneproject.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstoneproject.R
import com.capstoneproject.utils.Constants
import com.google.firebase.auth.FirebaseAuth

class DashboardActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val rl_screening:RelativeLayout = findViewById(R.id.rl_screening)
        val rl_history:RelativeLayout = findViewById(R.id.rl_history)
        val rl_about_us:RelativeLayout = findViewById(R.id.rl_about_us)
        val rl_logout:RelativeLayout = findViewById(R.id.rl_logout)
        val nama: TextView = findViewById(R.id.nama)
        val nik: TextView = findViewById(R.id.NIK)


        val sharedPreferences = getSharedPreferences(Constants.NAME_PREFERENCES, Context.MODE_PRIVATE)
        val username = sharedPreferences.getString(Constants.LOGGED_USERNAME, "")!!
        val Nik = sharedPreferences.getString(Constants.NIK, "")!!
        nama.text = username
        nik.text = Nik

        rl_screening.setOnClickListener {
            startActivity(Intent(applicationContext, PredictionSelectionActivity::class.java))
        }

        rl_history.setOnClickListener {
            startActivity(Intent(applicationContext, HistoryActivity::class.java))
        }

        rl_about_us.setOnClickListener {
            startActivity(Intent(applicationContext, AboutActivity::class.java))
        }

        rl_logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(applicationContext, LoginActivity::class.java))
            Toast.makeText(this,"Logout success.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

}