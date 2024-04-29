package com.example.whatsappclone.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.whatsappclone.fragments.Calls
import com.example.whatsappclone.fragments.Chats
import com.example.whatsappclone.fragments.Status

class FragmentAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm!!) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                Chats()
            }
            1 -> {
                Status()
            }
            else -> {
                Calls()
            }
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var title = ""
        title = when (position) {
            0 -> {
                "Chats"
            }

            1 -> {
                "Status"
            }

            else -> {
                "Calls"
            }
        }
        return title
    }
}
