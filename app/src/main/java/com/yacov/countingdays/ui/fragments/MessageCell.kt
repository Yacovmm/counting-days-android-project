package com.yacov.countingdays.ui.fragments

import android.view.View
import android.widget.TextView
import com.yacov.countingdays.R
import com.yacov.countingdays.data.entities.MessageEntity
import com.yacov.countingdays.ui.adapters.BaseCell

class MessageCell(view: View) : BaseCell(view) {

    companion object {
        val resId = R.layout.message_item_cell
    }

    private var titleText: TextView = view.findViewById(R.id.title_tv)
    private var msgText: TextView = view.findViewById(R.id.msg_tv)

    fun setModel(messageEntity: MessageEntity) {

        titleText.text = messageEntity.title
        msgText.text = "${messageEntity.msg.slice(0..70)}..."
    }
}
