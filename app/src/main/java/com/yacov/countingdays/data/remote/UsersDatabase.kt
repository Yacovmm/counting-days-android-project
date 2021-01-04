package com.yacov.countingdays.data.remote

import android.provider.ContactsContract
import com.google.firebase.firestore.FirebaseFirestore
import com.yacov.countingdays.data.entities.UserEntity
import com.yacov.countingdays.utils.Constants
import kotlinx.coroutines.tasks.await

class UsersDatabase {

    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection(Constants.USERS_COLLECTIONS)

    suspend fun getUsers(): List<UserEntity> =
        try {
            usersCollection.get().await().toObjects(UserEntity::class.java)
        } catch (e: Exception) {
            emptyList()
        }

    suspend fun checkIfUserExists(nickname: String): Boolean =
        try {
            val result = usersCollection.whereEqualTo("nickname", nickname).get().await().toObjects(UserEntity::class.java)
            result.isNotEmpty()
        } catch (e: Exception) {
            false
        }

    suspend fun saveUser(userEntity: UserEntity): Boolean {
        return try {
            usersCollection.document().set(userEntity).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
