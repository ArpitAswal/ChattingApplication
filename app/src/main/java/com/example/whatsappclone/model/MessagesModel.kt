package com.example.whatsappclone.model

class MessagesModel {

    var id: String ?= null
    var msg: String ?= null
    var time: String ?= null

    constructor()  {
        id=  ""
        msg = ""
        time = ""
    }

    constructor(id: String, msg: String, time: String){
        this.id = id
        this.msg = msg
        this.time = time
    }

    constructor(id: String, msg: String){
        this.id = id
        this.msg = msg
    }


}