package com.example.makro123.payapp

import android.content.Context
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import com.example.makro123.payapp.Classes.Transaction
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_trasnactions.*
import kotlinx.android.synthetic.main.transaction_details_ticket.view.*

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
            }
        } catch (ex: Exception) {
        }

        // TODO remove later
        loadData()

        // Set Adapter for ViewList
        adapter = TransactionAdapter(listOfTransactions, this)
        lvTransactions.adapter = adapter
    }

    fun loadData() {
        listOfTransactions.add(Transaction(1, 1, "1234543134334324123", 100000.00, true))
        listOfTransactions.add(Transaction(2, 1, "jgk903g934ug04g943g", 150000.00, false))
        listOfTransactions.add(Transaction(3, 1, "g9-40i9-04jg023t94g", 1000000.00, false))
        listOfTransactions.add(Transaction(4, 1, "0-34inripg024jg4-92", 200.00, true))
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
                true -> {
                    amount = "+" + transaction.getAmount()
                    transactionView.tvAmount.setTextColor(Color.GREEN)
                }
                false -> {
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
