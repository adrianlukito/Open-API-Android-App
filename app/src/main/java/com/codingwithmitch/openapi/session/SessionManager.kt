package com.codingwithmitch.openapi.session

import android.app.Application
import com.codingwithmitch.openapi.persistence.AuthTokenDao
import javax.inject.Inject

class SessionManager @Inject constructor(
    val authTokenDao: AuthTokenDao,
    val application: Application
) {
}