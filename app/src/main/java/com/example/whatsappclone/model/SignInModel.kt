package com.example.whatsappclone.model

class SignInModel{

    var number: String? = null
    var signinUserId: String? = null

    constructor()

    // Constructor for all variables.
    constructor(number: String?, signinUserId: String?) {
        this.number = number
        this.signinUserId = signinUserId
    }

}

