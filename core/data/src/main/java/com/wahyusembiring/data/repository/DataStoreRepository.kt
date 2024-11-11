package com.wahyusembiring.data.repository

import com.wahyusembiring.data.Result
import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {

    fun saveOnBoardingState(completed: Boolean): Flow<Result<Unit>>

    fun readOnBoardingState(): Flow<Result<Flow<Boolean>>>

}