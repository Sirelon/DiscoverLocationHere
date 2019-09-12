package com.sirelon.discover.location.feature.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.json.JSONObject


open class BaseViewModel : ViewModel() {

    private val job = SupervisorJob()

    val safetyIOContext =
        Dispatchers.IO + job + CoroutineExceptionHandler { _, e ->
            onError(e)
        }

    // Single LiveData should be here
    val errosLiveData = MutableLiveData<Throwable>()

    fun onError(e: Throwable) {
        e.printStackTrace()
        // Actually I would like to parse error in some util method, or somewhere in API class or repository, but this is simple project, right?
        if (e is retrofit2.HttpException) {
            try {
                val json = JSONObject(e.response()?.errorBody()?.string())
                val message = json.getString("message")
                errosLiveData.postValue(RuntimeException(message))
            } catch (parseErr: Exception) {
                errosLiveData.postValue(e)
            }
        } else {
            errosLiveData.postValue(e)
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}