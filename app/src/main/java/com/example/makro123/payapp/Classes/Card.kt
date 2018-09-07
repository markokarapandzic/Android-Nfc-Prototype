package com.example.makro123.payapp.Classes

class Card {

    private var cardId: Int? = null
    private var userId: Int? = null
    private var accountNumber: String? = null
    private var expirationDate: String? = null
    private var cvc: Int? = null
    private var bank: String? = null
    private var holderName: String? = null
    private var amount: Double? = null

    constructor(cardId: Int, userId: Int, accountNumber: String, expirationDate: String, cvc: Int, bank: String, holderName: String, amount: Double) {
        this.cardId = cardId
        this.userId = userId
        this.accountNumber = accountNumber
        this.expirationDate = expirationDate
        this.cvc = cvc
        this.bank = bank
        this.holderName = holderName
        this.amount = amount
    }

    fun getCardId(): Int? {
        return this.cardId
    }

    fun setCardId(cardId: Int) {
        this.cardId = cardId
    }

    fun getUserId(): Int? {
        return this.userId
    }

    fun setUserId(userId: Int) {
        this.userId = userId
    }

    fun getAccountNumber(): String? {
        return this.accountNumber
    }

    fun setAccountNumber(accountNumber: String) {
        this.accountNumber = accountNumber
    }

    fun getExpirationDate(): String? {
        return this.expirationDate
    }

    fun setExpirationDate(expirationDate: String) {
        this.expirationDate = expirationDate
    }

    fun getCvc(): Int? {
        return this.cvc
    }

    fun setCvc(cvc: Int) {
        this.cvc = cvc
    }

    fun getBank(): String? {
        return this.bank
    }

    fun setBank(bank: String) {
        this.bank = bank
    }

    fun getHolderName(): String? {
        return this.holderName
    }

    fun setHolderName(holderName: String) {
        this.holderName = holderName
    }

    fun getAmount(): Double? {
        return this.amount
    }

    fun setAmount(amount: Double) {
        this.amount = amount
    }

}