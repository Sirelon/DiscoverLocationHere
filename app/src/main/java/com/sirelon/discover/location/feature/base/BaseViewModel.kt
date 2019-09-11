package com.sirelon.discover.location.feature.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.json.JSONObject


open class BaseViewModel : ViewModel() {

    // Single LiveData should be here
    val errosLiveData = MutableLiveData<Throwable>()

    fun onError(e: Throwable) {
        e.printStackTrace()
        // Actually I would like to parse error in some util method, or somewhere in API class or repository, but this is simple project, right?
        if (e is retrofit2.HttpException) {
            val json = JSONObject(e.response()?.errorBody()?.string())
            val message = json.getString("message")
            errosLiveData.postValue(RuntimeException(message))
        } else {
            errosLiveData.postValue(e)
        }
    }
}