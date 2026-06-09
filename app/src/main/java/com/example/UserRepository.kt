package com.example

import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {
    val allUsers: Flow<List<UserEntity>> = userDao.getAllUsers()

    suspend fun insert(user: UserEntity) = userDao.insertUser(user)

    suspend fun deleteById(id: Int) = userDao.deleteUserById(id)
}
