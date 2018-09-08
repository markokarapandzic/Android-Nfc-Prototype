package com.example.makro123.payapp.Classes

class Transaction {

    private var transactionId: Int? = null
    private var cardId: Int? = null
    private var trans: String? = null
    private var amount: Double? = null
    private var pay: Int? = null

    constructor(transactionId: Int?, cardId: Int?, trans: String?, amount: Double?, pay: Int?) {
        this.transactionId = transactionId
        this.cardId = cardId
        this.trans = trans
        this.amount = amount
        this.pay = pay
    }

    fun getTransactionId(): Int? {
        return this.transactionId
    }

    fun setTransactionId(transactionId: Int) {
        this.transactionId = transactionId
    }

    fun getCardId(): Int? {
        return this.cardId
    }

    fun setCardId(cardId: Int) {
        this.cardId = cardId
    }

    fun getTrans(): String? {
        return this.trans
    }

    fun setTrans(trans: String) {
        this.trans = trans
    }

    fun getAmount(): Double? {
        return this.amount
    }

    fun setAmount(amount: Double) {
        this.amount = amount
    }

    fun isPayment(): Int? {
        return this.pay
    }

    fun setPayment(pay: Int) {
        this.pay = pay
    }

}