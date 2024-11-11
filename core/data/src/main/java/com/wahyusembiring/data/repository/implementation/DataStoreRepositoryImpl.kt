package com.wahyusembiring.data.repository.implementation

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.wahyusembiring.data.Result
import com.wahyusembiring.data.repository.DataStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "habit_preferences")

class DataStoreRepositoryImpl @Inject constructor(
    private val appContext: Context
) : DataStoreRepository {

    private object PreferencesKey {
        val onBoardingKey = booleanPreferencesKey(name = "on_boarding_completed")
    }

    private val dataStore = appContext.dataStore

    override fun saveOnBoardingState(completed: Boolean): Flow<Result<Unit>> {
        return flow {
            emit(Result.Loading())
            dataStore.edit { preferences ->
                preferences[PreferencesKey.onBoardingKey] = completed
            }
            emit(Result.Success(Unit))
        }.catch {
            emit(Result.Error(it))
        }
    }

    override fun readOnBoardingState(): Flow<Result<Flow<Boolean>>> {
        return flow<Result<Flow<Boolean>>> {
            emit(Result.Loading())
            val onBoardingState = dataStore.data.map { preferences ->
                preferences[PreferencesKey.onBoardingKey] ?: false
            }
            emit(Result.Success(onBoardingState))
        }.catch {
            emit(Result.Error(it))
        }
    }
}