package com.example.makro123.payapp

import android.content.Context
import android.graphics.Color
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import com.example.makro123.payapp.Classes.Transaction
import kotlinx.android.synthetic.main.activity_trasnactions.*
import kotlinx.android.synthetic.main.transaction_details_ticket.view.*
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

class TrasnactionsActivity : AppCompatActivity() {

    var cardId: Int? = null
    var listOfTransactions = ArrayList<Transaction>()
    var adapter: TransactionAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trasnactions)

        // Get Data from Bundle
        try {
            val bundle: Bundle = intent.extras
            cardId = bundle.getInt("card", 0)

            if (cardId == 0) {
                Toast.makeText(this, "No Transaction Data", Toast.LENGTH_LONG).show()
            } else {
                val url = "https://pay-app-api.herokuapp.com/transaction/card/$cardId"
                AsyncTaskHandlerJsonGet().execute(url)
            }

        } catch (ex: Exception) { }

        // TODO remove later
//        loadData()

        // Set Adapter for ViewList
        adapter = TransactionAdapter(listOfTransactions, this)
        lvTransactions.adapter = adapter
    }

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
            listOfTransactions.add(Transaction(
                    jsonObject.getInt("transaction_id"),
                    jsonObject.getInt("card_id"),
                    jsonObject.getString("trans"),
                    jsonObject.getDouble("amount"),
                    jsonObject.getInt("pay")
            ))
            x++
        }

        adapter!!.notifyDataSetChanged()
    }

    inner class TransactionAdapter : BaseAdapter {

        private var listOfTransaction = ArrayList<Transaction>()
        private var context: Context? = null

        constructor (listOfTransaction: ArrayList<Transaction>, context: Context?) : super() {
            this.listOfTransaction = listOfTransaction
            this.context = context
        }


        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

            // Import View
            val transaction = this.listOfTransaction[position]
            val inflator = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val transactionView = inflator.inflate(R.layout.transaction_details_ticket, null)

            // Change values in layout(view)
            var amount: String? = null
            when (transaction.isPayment()) {
                0 -> {
                    amount = "+" + transaction.getAmount()
                    transactionView.tvAmount.setTextColor(Color.GREEN)
                }
                1 -> {
                    amount = "-" + transaction.getAmount()
                    transactionView.tvAmount.setTextColor(Color.RED)
                }
            }

            // Set Data
            transactionView.tvTransCode.text = transaction.getTrans()
            transactionView.tvAmount.text = amount

            return transactionView
        }

        override fun getItem(position: Int): Any {
            return this.listOfTransaction[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return this.listOfTransaction.size
        }
    }

}
