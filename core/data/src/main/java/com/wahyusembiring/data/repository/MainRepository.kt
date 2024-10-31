package com.wahyusembiring.data.repository

import kotlinx.coroutines.flow.Flow
import com.wahyusembiring.data.Result

interface MainRepository {

    suspend fun syncToLocal(): Flow<Result<Unit>>

    suspend fun syncToCloud(): Flow<Result<Unit>>

}