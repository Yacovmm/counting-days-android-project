package com.yacov.countingdays.data.entities

import java.io.Serializable
import java.time.LocalDateTime

data class MessageEntity(
    var sender: String = String(),
    var userReceiver: String = String(),
    var dayOfWeek: Int? = null,
    var msg: String = String(),
    var imageUrl: String = String(),
    var title: String = String(),
    var createdAt: String = LocalDateTime.now().toString()
) : Serializable
