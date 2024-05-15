package com.example.whatsappclone.model

import java.io.Serializable

class GroupModel : Serializable{

    var groupId: String? = null

    var groupName: String? = null

    var groupProfile: String? = null

    var groupMembers: ArrayList<ContactSaved>

    constructor(){
        groupId = ""
        groupMembers = ArrayList<ContactSaved>()
        groupName = ""
        groupProfile = ""
    }

    constructor(groupId: String?,groupMembers: ArrayList<ContactSaved>, groupName: String?, groupProfile: String?) {
        this.groupId = groupId
        this.groupName = groupName
        this.groupProfile = groupProfile
        this.groupMembers = groupMembers
    }
}