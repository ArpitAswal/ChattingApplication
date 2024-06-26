package com.example.whatsappclone.model

class MessagesModel{

    var owner: String?= null
    var id: String ?= null
    var msg: String ?= null
    var time: String ?= null

    constructor() {
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true // Check if it's the same instance
        if (other !is MessagesModel) return false // Check if it's not the same type

        // Compare individual fields for equality
        return owner == other.owner &&
                id == other.id &&
                msg == other.msg &&
                time == other.time
    }

    // Also override hashCode for hash-based collections
    override fun hashCode(): Int {
        var result = owner?.hashCode() ?: 0
        result = 31 * result + (id?.hashCode() ?: 0)
        result = 31 * result + (msg?.hashCode() ?: 0)
        result = 31 * result + (time?.hashCode() ?: 0)
        return result
    }

}