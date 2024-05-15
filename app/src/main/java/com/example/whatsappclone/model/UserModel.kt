package com.example.whatsappclone.model

class UserModel(val profileImg: String, val username: String, val userId: String, var userLastMsg: String, val source: ListType)

enum class ListType {
    Individual,
    Group
}