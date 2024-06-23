package com.example.whatsappclone.model

import com.google.firebase.Timestamp

class UserModel(val profileImg: String, val username: String, val userId: String, var userLastMsg: String, val source: ListType, var chatTime: String)

enum class ListType {
    Individual,
    Group
}