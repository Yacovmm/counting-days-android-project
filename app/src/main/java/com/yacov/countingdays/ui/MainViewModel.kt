package com.yacov.countingdays.ui

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yacov.countingdays.R
import com.yacov.countingdays.data.entities.MessageEntity
import com.yacov.countingdays.data.entities.UserEntity
import com.yacov.countingdays.data.remote.MessagesDatabase
import com.yacov.countingdays.data.remote.UsersDatabase
import com.yacov.countingdays.ui.adapters.GenericAdapter
import com.yacov.countingdays.utils.ViewState
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Month
import java.time.temporal.ChronoUnit

class MainViewModel : ViewModel() {

    var adapter: GenericAdapter<MessageEntity>? = null
    private val messagesLive = MutableLiveData<List<MessageEntity>>()
    val messagesLiveData: LiveData<List<MessageEntity>>
        get() = messagesLive
    fun getMessages(userReceiver: String) = viewModelScope.launch {
        val messagesList = MessagesDatabase().getTodayMessagesToUser(userReceiver)
        messagesLive.postValue(messagesList)
    }

    private val messageSavedLive = MutableLiveData<Boolean>()
    val messageSavedLiveData: LiveData<Boolean>
        get() = messageSavedLive
    fun saveMessage(msg: MessageEntity) = viewModelScope.launch {
        val savedItem = MessagesDatabase().saveMessage(msg)
        if (savedItem) messageSavedLive.postValue(true) else messageSavedLive.postValue(false)
    }

    fun getDaysResting(): String {
        val todayLocalData = LocalDate.now()
        val weddingLocalDate = LocalDate.of(2021, Month.JANUARY, 10)

        val daysBetween = ChronoUnit.DAYS.between(weddingLocalDate, todayLocalData)
        return "$daysBetween dias para o dia mais especial da sua vida...".replace("-", "")
    }

    private val mutableUsers = MutableLiveData<List<UserEntity>>()
    val usersLiveData: LiveData<List<UserEntity>>
        get() = mutableUsers
    fun getUsers() = viewModelScope.launch {
        val users = UsersDatabase().getUsers()
        mutableUsers.postValue(users)
    }

    private val userRegisterlive = MutableLiveData<Boolean>()
    val registerLiveData: LiveData<Boolean>
        get() = userRegisterlive
    fun registerUser(nickName: String) = viewModelScope.launch {
        val userAlreadyExists = UsersDatabase().checkIfUserExists(nickName)
        if (userAlreadyExists) {
            userRegisterlive.postValue(false)
        } else {
            val registerStatus = UsersDatabase().saveUser(UserEntity(nickName))
            userRegisterlive.postValue(registerStatus)
        }
    }

    private val uploadImgLive = MutableLiveData<ViewState<String>>()
    val uploadImgLiveData: LiveData<ViewState<String>>
        get() = uploadImgLive
    fun uploadImageAndSaveMessage(uri: Uri) = viewModelScope.launch {
        uploadImgLive.postValue(ViewState.Loading())
        val downloadUrl = MessagesDatabase().storeFile(
            "IMG_${uri.lastPathSegment ?: "picture"}.jpg",
            uri
        )
        downloadUrl?.let {
            uploadImgLive.postValue(ViewState.Success(it))
        } ?: kotlin.run {
            uploadImgLive.postValue(ViewState.Error(R.string.error))
        }
    }
}
