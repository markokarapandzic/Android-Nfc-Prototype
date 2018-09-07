package com.example.makro123.payapp

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import com.example.makro123.payapp.Classes.Card
import kotlinx.android.synthetic.main.activity_add.*
import java.io.BufferedOutputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

class AddActivity : AppCompatActivity() {

    private val KEY_LOG_TEXT = "logText"
    private val currentCard: Card? = null
    val url = "https://pay-app-api.herokuapp.com/card/add?userId=2&accountNumber=1234123412341234&expirationDate=89/09&cvc=121&bank=SwisBank&holderName=Jovan%20Jovanovic"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        val nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        logMessage("NFC supported", (nfcAdapter != null).toString())
        logMessage("NFC enabled", (nfcAdapter?.isEnabled).toString())

        scrollDown()

        // Save Data
        AsyncTaskHandlerJsonGet().execute(url)

        if (intent != null)
            processIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if (intent != null)
            processIntent(intent)
    }

    private fun processIntent(checkIntent: Intent) {
        // Check if intent has the action of a discovered NFC tag
        // with NDEF formatted contents
        if (checkIntent.action == NfcAdapter.ACTION_NDEF_DISCOVERED) {

            // Retrieve the raw NDEF message from the tag
            val rawMessages = checkIntent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            val ndefMsg = rawMessages[0] as NdefMessage
            val ndefRecord = ndefMsg.records[0]

            if (ndefRecord.toUri() != null)
                logMessage("URI Detected", ndefRecord.toUri().toString())
            else
                logMessage("Payload", ndefRecord.payload.contentToString())

            // TODO Save Payload to Database as a User's Card
            AsyncTaskHandlerJsonGet().execute(url)
        }
    }

    inner class AsyncTaskHandlerJsonGet: AsyncTask<String, String, String>() {

        override fun doInBackground(vararg params: String?): String {
            val text: String
            val connection = URL(params[0]).openConnection() as HttpURLConnection

            try {
                connection.connect()
                text = connection.inputStream.use { it.reader().use { reader -> reader.readText() } }
            } finally {
                connection.disconnect()
            }

            return text
        }
    }

    // ==========================================================================
    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putCharSequence(KEY_LOG_TEXT, tv_messages.text)
        super.onSaveInstanceState(outState)
    }

    private fun logMessage(header: String, text: String?) {
        tv_messages.append(if (text.isNullOrBlank()) fromHtml("<b>$header</b><br>") else fromHtml("<b>$header</b>: $text<br>"))
        scrollDown()
    }

    private fun fromHtml(html: String): Spanned {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
        } else {
            @Suppress("DEPRECATION")
            Html.fromHtml(html)
        }
    }

    private fun scrollDown() {
        sv_messages.post({ sv_messages.smoothScrollTo(0, sv_messages.bottom) })
    }
}
