package com.example.composetutorial.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel (appDatabase: Application) : AndroidViewModel(appDatabase){
    private val userRepository : AppDatabase.UserRepository
    init {
        val userDao = AppDatabase.AppDatabase.getDatabase(appDatabase).userDao()
        userRepository = AppDatabase.UserRepository(userDao)
    }

    fun addUser(user: AppDatabase.User){
        viewModelScope.launch(Dispatchers.IO){
            userRepository.addUser(user)
        }
    }

    suspend fun getUser(userID: Int): AppDatabase.User{
        return  userRepository.getUser(userID)
    }

}