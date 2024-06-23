package com.example.whatsappclone.model

import com.google.firebase.Timestamp

class MessagesModel {
    var owner: String?= null
    var id: String ?= null
    var msg: String ?= null
    var time: String ?= null

    constructor()  {
        owner= ""
        id=  ""
        msg = ""
        time = ""
    }

    constructor(owner: String, id: String, msg: String, time: String){
        this.owner = owner
        this.id = id
        this.msg = msg
        this.time = time
    }


}