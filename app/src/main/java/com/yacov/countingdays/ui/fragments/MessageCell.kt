package com.yacov.countingdays.ui.fragments

import android.view.View
import android.widget.TextView
import com.yacov.countingdays.R
import com.yacov.countingdays.data.entities.MessageEntity
import com.yacov.countingdays.ui.adapters.BaseCell
import java.text.SimpleDateFormat
import java.util.*

class MessageCell(view: View) : BaseCell(view) {

    companion object {
        val resId = R.layout.message_item_cell
    }

    private var titleText: TextView = view.findViewById(R.id.title_tv)
    private var msgText: TextView = view.findViewById(R.id.msg_tv)
    private var dateTxt: TextView = view.findViewById(R.id.date_tv)

    fun setModel(messageEntity: MessageEntity) {

        titleText.text = messageEntity.title
        if (messageEntity.msg.length > 70)
            msgText.text = "${messageEntity.msg.slice(0..70)}..."
        else
            msgText.text = messageEntity.msg

        dateTxt.text = "Enviada em: ${formatDate(messageEntity.createdAt, outFormat = "dd/MM/dd HH:mm")}"
    }

    fun formatDate(date: Date, outFormat: String = "yyyy-MM-dd"): String {
        val simpleDateFormat = SimpleDateFormat(outFormat, Locale("pt", "BR"))
        return simpleDateFormat.format(date)
    }

    fun formatDate(dateString: String, dateStringFormat: String = "yyyy-MM-dd'T'HH:mm:ss.SSS", outFormat: String = "yyyy-MM-dd"): String {
        val sdf = SimpleDateFormat(dateStringFormat, Locale("pt", "BR"))
        val date = sdf.parse(dateString) ?: Date()

        return formatDate(date, outFormat)
    }
}
