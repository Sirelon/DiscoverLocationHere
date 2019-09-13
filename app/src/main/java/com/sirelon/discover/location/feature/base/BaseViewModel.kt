package com.sirelon.discover.location.feature.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.json.JSONObject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


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
        if (isConnectionException(e)) {
            errosLiveData.postValue(RuntimeException("No connection!"))
        } else if (e is retrofit2.HttpException) {
            // Actually I would like to parse error in some util method, or somewhere in API class or repository, but this is simple project, right?
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

    private fun isConnectionException(exception: Throwable): Boolean {
        val isConnectionException =
            exception is ConnectException || exception is SocketTimeoutException || exception is UnknownHostException
        return isConnectionException
    }
}


