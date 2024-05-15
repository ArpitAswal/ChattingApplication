package com.example.whatsappclone.model

import java.io.Serializable

class StatusModel : Serializable {
    var userName: String? = null
    var userId: String? = null
    var imageUrl: String? = null
    var time: String? = null

    constructor(){
        userName = ""
        userId = ""
        imageUrl = ""
        time = ""
    }

    constructor(userName: String, userId: String, imageUrl: String, time:String){
        this.userName = userName
        this.userId = userId
        this.imageUrl = imageUrl
        this.time = time
    }
}
