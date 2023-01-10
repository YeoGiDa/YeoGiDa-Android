package com.starters.yeogida.presentation.user

import com.google.android.gms.auth.api.signin.GoogleSignInOptions

object GoogleLoginObj {
    val signInOption by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestId()
            .build()
    }
}