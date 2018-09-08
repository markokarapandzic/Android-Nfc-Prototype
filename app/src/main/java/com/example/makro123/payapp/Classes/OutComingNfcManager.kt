package com.example.makro123.payapp.Classes

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.NfcEvent

class OutcomingNfcManager(
        private val nfcActivity: NfcActivity
) :
        NfcAdapter.CreateNdefMessageCallback,
        NfcAdapter.OnNdefPushCompleteCallback {

    override fun createNdefMessage(event: NfcEvent): NdefMessage {
        // creating outcoming NFC message with a helper method
        // you could as well create it manually and will surely need, if Android version is too low
        val card = nfcActivity.getOutcomingMessage()
        val accountNumber = card!!.getAccountNumber()
        val expirationDate = card.getExpirationDate()
        val cvc = card.getCvc()

        val outString = accountNumber + expirationDate + cvc

        with(outString) {
            val outBytes = this.toByteArray()
            val outRecord = NdefRecord.createMime("text/plain", outBytes)
            return NdefMessage(outRecord)
        }
    }

    override fun onNdefPushComplete(event: NfcEvent) {
        // onNdefPushComplete() is called on the Binder thread, so remember to explicitly notify
        // your view on the UI thread
        nfcActivity.signalResult()
    }


    /*
    * Callback to be implemented by a Sender activity
    * */
    interface NfcActivity {
        fun getOutcomingMessage(): Card?
        fun signalResult()
    }
}