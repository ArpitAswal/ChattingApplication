package com.example.whatsappclone.model

class MessagesModel {

    var id: String ?= null
    var msg: String ?= null
    var time: Long ?= null

    constructor()  {
        id=  ""
        msg = ""
        time = 0L
    }

    constructor(id: String, msg: String, time: Long){
        this.id = id
        this.msg = msg
        this.time = time
    }

    constructor(id: String, msg: String){
        this.id = id
        this.msg = msg
    }


}