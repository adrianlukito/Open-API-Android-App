package com.codingwithmitch.openapi.session

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.persistence.AuthTokenDao
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    val authTokenDao: AuthTokenDao,
    val application: Application
) {

    private val TAG = "AppDebug"

    private val _cachedToken = MutableLiveData<AuthToken>()

    val cachedToken: LiveData<AuthToken>
        get() = _cachedToken

    fun login(newValue: AuthToken) {
        setValue(newValue)
    }
    
    fun logout() {
        Log.d(TAG, "logout...")
        GlobalScope.launch(IO) {
            var errorMessage: String? = null
            
            try {
                _cachedToken.value!!.account_pk?.let {
                    Log.d(TAG, "nullify: ")
                    authTokenDao.nullifyToken(it)
                } ?: throw CancellationException("Token Error. Logging out user.")
            } catch (e: CancellationException) {
                Log.e(TAG, "logout: ${e.message}")
                errorMessage = e.message
            } catch (e: Exception) {
                Log.e(TAG, "logout: ${e.message}")
                errorMessage = e.message
            } finally {
                errorMessage?.let {
                    Log.e(TAG, "logout: $errorMessage", )
                }
                Log.d(TAG, "logout: finally...")
                setValue(null)
            }
        }
    }

    private fun setValue(newValue: AuthToken?) {
        Log.d(TAG, "login: TRLALALA $newValue")
        GlobalScope.launch(Main) {
            Log.d(TAG, "setValue: ${_cachedToken.value} -- ${newValue}")
            if(_cachedToken.value != newValue) {
                _cachedToken.value = newValue
            }
        }
    }

    fun isConnectedToTheInternet(): Boolean {
        val cm = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        try {
            return cm.activeNetworkInfo.isConnected
        }catch (e: Exception) {
            Log.e(TAG, "isConnectedToTheInternet: ${e.message}", )
        }
        return false
    }
}