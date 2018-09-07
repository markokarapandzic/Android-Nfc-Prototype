package com.example.makro123.payapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import butterknife.BindView
import butterknife.ButterKnife
import android.content.Intent
import android.os.AsyncTask
import android.view.View
import android.widget.*
import com.example.makro123.payapp.Classes.User
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

class SignupActivity : AppCompatActivity() {

    private val list = ArrayList<User>()
    private val url = "http://pay-app-api.herokuapp.com/user"

    //private val urlPo = "https://pay-app-api.herokuapp.com/user/add?firstName=Nikola&lastName=Nikolic&email=nnikolic@gmail.com&password=test2&telephone=0652258694"
    @BindView(R.id.input_name)
    lateinit var nameText: EditText
    @BindView(R.id.input_lname)
    lateinit var lnameText: EditText
    @BindView(R.id.input_email)
    lateinit var emailText: EditText
    @BindView(R.id.input_mobile)
    lateinit var mobileText: EditText
    @BindView(R.id.input_password)
    lateinit var passwordText: EditText
    @BindView(R.id.input_reEnterPassword)
    lateinit var reEnterPasswordText: EditText
    @BindView(R.id.btn_signup)
    lateinit var signupButton: Button
    @BindView(R.id.progressBar)
    lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        ButterKnife.bind(this)
        AsyncTaskHandlerJsonGet().execute(url)
        progressBar.setVisibility(View.GONE)
    }

    fun buLoginClick(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out)
    }

    fun buRegister(view: View) {
        signup(view)
    }

    fun signup(view: View) {

        if (!validate()) {
            onSignupFailed()
            return
        }

        signupButton.setEnabled(false)

        progressBar = ProgressBar(this, null,
                R.style.AppTheme_Dark_Dialog)
        progressBar.isIndeterminate = true
        progressBar.setVisibility(View.VISIBLE)
        android.os.Handler().postDelayed(
                {
                    // On complete call either onSignupSuccess or onSignupFailed
                    // depending on success
                    onSignupSuccess(view)
                    progressBar.setVisibility(View.GONE);
                }, 3000)
    }

    fun onSignupSuccess(view: View) {
        val urlPost = "https://pay-app-api.herokuapp.com/user/add?firstName=" + nameText.text.toString() + "&lastName=" + lnameText.text.toString() +
                "&email=" + emailText.text.toString() + "&password=" + passwordText.text.toString() + "&telephone=" + mobileText.text.toString()
        AsyncTaskHandlerJsonPost().execute(urlPost)

        signupButton.setEnabled(true)
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out)
        finish()
    }

    fun onSignupFailed() {
        Toast.makeText(baseContext, "Register failed", Toast.LENGTH_LONG).show()
    }

    fun validate(): Boolean {
        var valid = true

        val name = nameText.text.toString()
        val lname = lnameText.text.toString()
        val email = emailText.text.toString()
        val mobile = mobileText.text.toString()
        val password = passwordText.text.toString()
        val reEnterPassword = reEnterPasswordText.text.toString()

        if (name.isEmpty() || name.length < 3) {
            nameText.setError("at least 3 characters")
            valid = false
        } else {
            nameText.setError(null)
        }

        if (lname.isEmpty()) {
            lnameText.setError("Enter Valid Last Name")
            valid = false
        } else {
            lnameText.setError(null)
        }


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() || !validateEmail(email)) {
            emailText.setError("Enter a valid email address")
            valid = false
        } else {
            emailText.setError(null)
        }

        if (mobile.isEmpty() || mobile.length != 10) {
            mobileText.setError("Enter Valid Mobile Number")
            valid = false
        } else {
            mobileText.setError(null)
        }

        if (password.isEmpty() || password.length < 4 || password.length > 10) {
            passwordText.setError("between 4 and 10 alphanumeric characters")
            valid = false
        } else {
            passwordText.setError(null)
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length < 4 || reEnterPassword.length > 10 || reEnterPassword != password) {
            reEnterPasswordText.setError("Password Do not match")
            valid = false
        } else {
            reEnterPasswordText.setError(null)
        }

        return valid
    }

    fun validateEmail(email: String): Boolean {
        var valid = true

        var x = 0
        while (x < list.size) {
            if (email == list[x].email) {
                valid = false
                break
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

    inner class AsyncTaskHandlerJsonPost : AsyncTask<String, String, String>() {
        override fun doInBackground(vararg params: String?): String {
            var text: String
            val connection = URL(params[0]).openConnection() as HttpURLConnection
            try {
                connection.connect()
                text = connection.inputStream.use { it.reader().use { reader -> reader.readText() } }
            } finally {
                connection.disconnect()
            }
            return "success"
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
