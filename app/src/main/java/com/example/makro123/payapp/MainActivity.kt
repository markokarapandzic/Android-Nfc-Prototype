package com.example.makro123.payapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.*
import com.example.makro123.payapp.Classes.Card
import com.example.makro123.payapp.Classes.OutcomingNfcManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.card_detail_ticket.view.*
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity(), OutcomingNfcManager.NfcActivity {

    private var currentCard: Card? = null
    private var userId: Int? = null
    private var listOfCard = ArrayList<Card>()
    private var adapter: CardAdapter? = null
    private var nfcAdapter: NfcAdapter? = null
    private lateinit var outcomingNfcCallback: OutcomingNfcManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check for NFC Adapter
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(this) as NfcAdapter

        // Data Reading
        try {
            val bundle: Bundle = intent.extras
            userId = bundle.getInt("userId")
            getDataFromUrl()
        } catch (ex: Exception) {}

        // Set adapter
        adapter = CardAdapter(listOfCard, this)
        lvCards.adapter = adapter

        // Encapsulate sending logic in a separate class
        this.outcomingNfcCallback = OutcomingNfcManager(this)
        this.nfcAdapter?.setOnNdefPushCompleteCallback(outcomingNfcCallback, this)
        this.nfcAdapter?.setNdefPushMessageCallback(outcomingNfcCallback, this)
    }

    private fun getDataFromUrl() {
        val url = "https://pay-app-api.herokuapp.com/card/user/$userId"
        AsyncTaskHandlerJsonGet().execute(url)
    }

    // NFC Stuff START
    override fun onNewIntent(intent: Intent) {
        this.intent = intent
    }

    override fun getOutcomingMessage(): Card? {
        return currentCard
    }

    fun setOutComingMessage(message: Card) {
        currentCard = message
    }

    override fun signalResult() {
        runOnUiThread {
            Toast.makeText(this, "Beaming Complete", Toast.LENGTH_SHORT).show()
        }
    }
    // NFC Stuff END

    // Data Downloading(JSON)
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

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            handleJson(result)
        }
    }

    fun handleJson(jsonString: String?) {
        val jsonArray = JSONArray(jsonString)
        var x = 0

        while (x < jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(x)
            listOfCard.add(Card(
                    jsonObject.getInt("card_id"),
                    jsonObject.getInt("user_id"),
                    jsonObject.getString("account_number"),
                    jsonObject.getString("expiration_date"),
                    jsonObject.getInt("cvc"),
                    jsonObject.getString("bank"),
                    jsonObject.getString("holder_name"),
                    jsonObject.getDouble("amount")
            ))
            x++
        }

        adapter!!.notifyDataSetChanged()
    }

    // Menu Handling
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Set mMenu for this Activity
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item!!.itemId) {
            R.id.mnAdd -> {
                val intent = Intent(this, AddActivity::class.java)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    // Adapter for Displaying Card Data
    inner class CardAdapter(private var listOfCards: ArrayList<Card>, context: Context) : BaseAdapter() {

        private var context: Context? = context

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

            // Import View
            val card = this.listOfCards[position]
            val inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val cardView = inflater.inflate(R.layout.card_detail_ticket, null)

            // Change values in layout(view)
            val amount = card.getAmount().toString() + "€"
            cardView.tvCardHolder.text = card.getHolderName()
            cardView.tvExpDate.text = card.getExpirationDate()
            cardView.tvAmount.text = amount

            // Handle Transaction Activity Display
            cardView.setOnLongClickListener {
                val intent = Intent(context, TrasnactionsActivity::class.java)
                intent.putExtra("card", card.getCardId())

                context!!.startActivity(intent)
                true
            }

            // Sending NFC Data(Payment)
            cardView.buPay.setOnClickListener {
                setOutComingMessage(card)
            }

            return cardView
        }

        override fun getItem(position: Int): Any {
            return this.listOfCards[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return this.listOfCards.size
        }
    }

}
