package com.example.whatsappclone.model

import java.io.Serializable

class CallModel : Serializable {
    var userName: String? = null
    var userId: String? = null
    var imageUrl: String? = null
    var dateTime: String? = null
    var callType: String? = null

    constructor(){
        userName = ""
        userId = ""
        imageUrl = ""
        dateTime = ""
        callType = ""
    }

    constructor(userName: String, userId: String, imageUrl: String, dateTime: String, callType: String){
        this.userName = userName
        this.userId = userId
        this.imageUrl = imageUrl
        this.dateTime = dateTime
        this.callType = callType
    }
}
