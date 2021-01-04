package com.yacov.countingdays.data.remote

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.yacov.countingdays.data.entities.MessageEntity
import com.yacov.countingdays.utils.Constants.LOVE_MESSAGES_COLLECTION
import kotlinx.coroutines.tasks.await
import java.util.*
import java.util.UUID.*

class MessagesDatabase {

    private val firestore = FirebaseFirestore.getInstance()
    private val messagesCollection = firestore.collection(LOVE_MESSAGES_COLLECTION)

    private val storageRef = FirebaseStorage.getInstance().getReference()

    suspend fun getAllMessages(dayOfWeek: Int? = null): List<MessageEntity> {

        dayOfWeek?.let { day ->
            return try {
                messagesCollection.whereEqualTo("dayOfWeek", day).get().await().toObjects(
                    MessageEntity::class.java
                )
            } catch (e: Exception) {
                emptyList()
            }
        } ?: kotlin.run {
            return try {
                messagesCollection.get().await().toObjects(MessageEntity::class.java)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    suspend fun getTodayMessagesToUser(user: String): List<MessageEntity> {
        return try {
            val calendar = Calendar.getInstance()
            val weekDay = calendar.get(Calendar.DAY_OF_WEEK)
            messagesCollection.whereEqualTo("userReceiver", user).whereEqualTo("dayOfWeek", weekDay).get().await().toObjects(
                MessageEntity::class.java
            )
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveMessage(msg: MessageEntity): Boolean {
        return try {
            messagesCollection.document().set(msg).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun storeFile(fileName: String, file: Uri): String? {
        return try {
            val ref = storageRef.child("images/${fileName}${randomUUID()}")
            val uploadTask = ref.putFile(file).await()
//            uploadTask.continueWithTask { task ->
//                if (!task.isSuccessful) {
//                    task.exception?.let {
//                        throw it
//                    }
//                }
//                ref.downloadUrl
//            }.await()
            val res = ref.downloadUrl.await().toString()
            res
        } catch (e: Exception) {
            null
        }
    }
}
