package com.capstoneproject.ui

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.capstoneproject.Firestore
import com.capstoneproject.R
import com.capstoneproject.model.User
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        val tv_register: TextView = findViewById(R.id.tv_register)
        val btn_login: Button = findViewById(R.id.btn_login)
        val email_login: EditText = findViewById(R.id.email_login)
        val password_login: EditText = findViewById(R.id.password_login)
        val progressBar: ProgressBar = findViewById(R.id.progresbar)


        tv_register.setOnClickListener(this)
        btn_login.setOnClickListener {
            val email = email_login.text.trim().toString()
            val pasword = password_login.text.trim().toString()


            if (TextUtils.isEmpty(email)){
                email_login.setError("Email is required")
                return@setOnClickListener

            }else if(TextUtils.isEmpty(pasword)){
                password_login.setError("Password is required")
                return@setOnClickListener

            } else if (pasword.length < 6){
                password_login.setError("Password must > 6 character")
            }
            progressBar.visibility = View.VISIBLE
            loginUser(email,pasword)
        }
    }
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_register -> {
                val moveIntent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(moveIntent)
            }
        }
    }

    fun loginUser(email: String, password: String){
        val progressBar: ProgressBar = findViewById(R.id.progresbar)
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this){task->
                    if (task.isSuccessful){
                        Firestore().getUserDetail(this@LoginActivity)


                    }else{
                        Toast.makeText(this,"Failed login."+task.exception, Toast.LENGTH_SHORT).show()
                        progressBar.visibility = View.GONE
                    }
                }

    }

    fun userLoginSucsess(user: User) {

        Log.i("email: ", user.email)
        Log.i("fullName", user.fullName)

        val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
        Toast.makeText(this,"Login successfully.", Toast.LENGTH_SHORT).show()
        startActivity(intent)
        finish()

    }
}