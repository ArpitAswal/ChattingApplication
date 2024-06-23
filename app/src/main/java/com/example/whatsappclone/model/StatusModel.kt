package com.example.whatsappclone.model

import com.google.firebase.Timestamp
import java.io.Serializable

class StatusModel : Serializable {
    var userName: String? = null
    var userId: String? = null
    var imageUrl: String? = null
    var time: Timestamp? = null

    constructor(){
        userName = ""
        userId = ""
        imageUrl = ""
        time = Timestamp.now()
    }

    constructor(userName: String, userId: String, imageUrl: String, time: Timestamp){
        this.userName = userName
        this.userId = userId
        this.imageUrl = imageUrl
        this.time = time
    }
}
