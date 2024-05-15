package com.example.whatsappclone.model

import java.io.Serializable

class ContactSaved : Serializable {

    var userid: String? = null

    var firstname: String? = null

    var lastname: String? = null

    var phone: String? = null

    var dp: String? = null

    var about: String? = null

    constructor(){
        userid=""
        firstname=""
        lastname=""
        phone=""
        dp=""
        about=""
    }
    constructor(userid: String?, firstname: String?, lastname: String?, phone: String?, dp: String?, about: String?) {
        this.userid = userid
        this.firstname = firstname
        this.lastname = lastname
        this.phone = phone
        this.dp = dp
        this.about = about
    }

}
