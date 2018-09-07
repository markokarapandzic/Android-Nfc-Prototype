package com.example.makro123.payapp

import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import com.example.makro123.payapp.Classes.User
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

class LoginActivity : AppCompatActivity() {

    private var userId: Int? = null
    private val list = ArrayList<User>()
    private val url = "http://pay-app-api.herokuapp.com/user"

    @BindView(R.id.input_email)
    lateinit var emailText: EditText
    @BindView(R.id.input_password)
    lateinit var passwordText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        ButterKnife.bind(this)
        AsyncTaskHandlerJsonGet().execute(url)
    }

    fun buRegisterClick(view: View) {
        val intent = Intent(this, SignupActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    fun buLogin(view: View) {
        if (!validate()) {
            onLoginFailed()
            return
        }

        val progressBar = ProgressBar(this, null,
                R.style.AppTheme_Dark_Dialog)
        progressBar.isIndeterminate = true
        progressBar.setVisibility(View.VISIBLE);

        android.os.Handler().postDelayed(
                {
                    onLoginSuccess()
                    progressBar.setVisibility(View.GONE);
                }, 3000)
    }


    fun onLoginSuccess() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }

    fun onLoginFailed() {
        Toast.makeText(baseContext, "Login failed", Toast.LENGTH_LONG).show()
    }

    fun validate(): Boolean {
        var valid = true

        val email = emailText.text.toString()
        val password = passwordText.text.toString()

        if (!validateEmail(email)) {
            emailText.setError("Enter a valid email address")
            valid = false
        } else {
            emailText.setError(null)
        }
        if (!validatePass(email, password)) {
            passwordText.setError("Enter a valid password")
            valid = false
        } else {
            passwordText.setError(null)
        }

        return valid

    }

    private fun validatePass(email: String, password: String): Boolean {
        var valid = false

        var x = 0
        while (x < list.size) {
            if (email == list[x].email) {
                if (password == list[x].password) {
                    userId = list[x].id
                    valid = true
                }
                return valid
            }
            x++
        }
        return valid
    }

    fun validateEmail(email: String): Boolean {
        var valid = false

        var x = 0
        while (x < list.size) {
            if (email == list[x].email) {
                valid = true
                return valid
            }
            x++
        }
        return valid
    }

    inner class AsyncTaskHandlerJsonGet : AsyncTask<String, String, String>() {
        override fun doInBackground(vararg params: String?): String {
            var text: String
            val connection = URL(params[0]).openConnection() as HttpURLConnection
            try {
                connection.connect()
                text = connection.inputStream.use { it.reader().use { reader -> reader.readText() } }
            } finally {
                connection.disconnect()
            }
            return text
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            handleJson(result)
        }

    }

    private fun handleJson(jsonString: String?) {

        val jsonArray = JSONArray(jsonString)
        var x = 0
        while (x < jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(x)
            list.add(User(
                    jsonObject.getInt("user_id"),
                    jsonObject.getString("first_name"),
                    jsonObject.getString("last_name"),
                    jsonObject.getString("email"),
                    jsonObject.getString("password"),
                    jsonObject.getString("phone")
            ))
            x++
        }
    }
}
