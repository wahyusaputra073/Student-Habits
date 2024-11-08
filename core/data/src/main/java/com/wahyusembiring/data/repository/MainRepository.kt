package com.wahyusembiring.data.repository

import kotlinx.coroutines.flow.Flow
import com.wahyusembiring.data.Result

interface MainRepository {

    @Deprecated("will be removed soon, fetch corresponding data directly from server when data is needed and then cache it to local")
    suspend fun syncToLocal(): Flow<Result<Unit>>

    @Deprecated("will be removed soon, upload corresponding data directly to server when data is changed and then cache it to local")
    suspend fun syncToCloud(): Flow<Result<Unit>>

}