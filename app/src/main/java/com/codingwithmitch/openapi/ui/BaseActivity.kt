package com.codingwithmitch.openapi.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.util.Constants.PERMISSIONS_REQUEST_READ_STORAGE
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BaseActivity: DaggerAppCompatActivity(), DataStateChangeListener, UICommunicationListener {
    val TAG = "AppDebug"

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onUIMessageReceived(uiMessage: UIMessage) {
        when(uiMessage.uiMessageType) {
            is UIMessageType.AreYouSureDialog -> {
                areYouSureDialog(uiMessage.message, uiMessage.uiMessageType.callback)
            }
            is UIMessageType.Toast -> {
                displayToast(uiMessage.message)
            }
            is UIMessageType.Dialog -> {
                displayInfoDialog(uiMessage.message)
            }
            is UIMessageType.None -> {
                Log.i(TAG, "onUIMessageReceived: ${uiMessage.message}")
            }
        }
    }

    override fun onDataStateChange(dataState: DataState<*>?) {
        dataState?.let { dataState ->
            GlobalScope.launch(Main) {
                displayProgressBar(dataState.loading.isLoading)

                dataState.error?.let { errorEvent ->
                    handleStateError(errorEvent)
                }

                dataState.data?.let { data ->
                    data.response?.let { event ->
                        handleStateResponse(event)
                    }
                }
            }
        }
    }

    fun handleStateError(errorEvent: Event<StateError>) {
        errorEvent.getContentIfNotHandled()?.let {
            when(it.response.responseType) {
                is ResponseType.Toast -> {
                    it.response.message?.let { message ->
                        displayToast(message)
                    }
                }
                is ResponseType.Dialog -> {
                    it.response.message?.let { message ->
                        displayErrorDialog(message)
                    }
                }
                is ResponseType.None -> {
                    Log.e(TAG, "handleStateError: ${it.response.message}", )
                }
            }
        }
    }

    fun handleStateResponse(event: Event<Response>) {
        event.getContentIfNotHandled()?.let {
            when(it.responseType) {
                is ResponseType.Toast -> {
                    it.message?.let { message ->
                        displayToast(message)
                    }
                }
                is ResponseType.Dialog -> {
                    it.message?.let { message ->
                        displaySuccessDialog(message)
                    }
                }
                is ResponseType.None -> {
                    Log.e(TAG, "handleStateError: ${it.message}", )
                }
            }
        }
    }

    abstract fun displayProgressBar(bool: Boolean)

    override fun hideSoftKeyboard() {
        if(currentFocus != null) {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }

    override fun isStoragePermissioGranted(): Boolean {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                PERMISSIONS_REQUEST_READ_STORAGE
            )

            return false
        } else {
            return true
        }
    }
}