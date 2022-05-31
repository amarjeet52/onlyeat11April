package com.codebrew.clikat.app_utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.api.AddressBean
import com.codebrew.clikat.data.model.others.GoogleLoginInput
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.module.login.GOOGLE_SIGN_IN
import com.codebrew.clikat.module.login.LoginViewModel
import com.codebrew.clikat.preferences.DataNames
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import javax.inject.Inject

const val TAG = "gooogle_login"

class GoogleLoginHelper @Inject constructor(private val mContext: Context) {


    private var loginInput: ((GoogleLoginInput) -> Unit?)? = null

    @Inject
    lateinit var mPreferenceHelper: PreferenceHelper

    private var mGoogleSignInClient: GoogleSignInClient? = null

    private lateinit var activity: Activity

    private lateinit var auth: FirebaseAuth

    private var mLoginViewModel: LoginViewModel? = null

    fun initialize(activity: Activity, clientId: String) {
        this.activity = activity

        val gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(clientId)
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);

        auth = FirebaseAuth.getInstance()
    }

    fun handleClick(mLoginViewModel: LoginViewModel?, loginInput: (GoogleLoginInput) -> Unit = {}) {

        this.loginInput = loginInput

        if (mLoginViewModel != null) {
            this.mLoginViewModel = mLoginViewModel
        }

        logoutGoogle {
            val signInIntent = mGoogleSignInClient?.signInIntent
            activity.startActivityForResult(signInIntent, GOOGLE_SIGN_IN)
        }
    }

    fun activityResult(requestCode: Int, resultCode: Int, data: Intent?, loginError: (String) -> Unit) {

        if (requestCode == GOOGLE_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task, loginError)
        } else {
            //loginError.invoke("Sign in Error")
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>, loginError: (String) -> Unit) {

        try {
            // Google Sign In was successful, authenticate with Firebase
            val account = completedTask.getResult(ApiException::class.java)!!
            // Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
            firebaseAuthWithGoogle(account.idToken ?: "", loginError)
        } catch (e: ApiException) {
            // Google Sign In failed, update UI appropriately
            //Log.w(TAG, "Google sign in failed", e)
            // [START_EXCLUDE]
            loginError.invoke(e.message ?: "")
            updateUI(null)
            // [END_EXCLUDE]
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String, loginError: (String) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        Log.d(TAG, auth.currentUser.toString())
                        val user = auth.currentUser
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        loginError.invoke(task.exception?.message ?: "")
                        updateUI(null)
                    }

                }
    }


    private fun updateUI(account: FirebaseUser?) {

        val acct = account ?: return

        val param = GoogleLoginInput()

        val mLocUser = mPreferenceHelper.getGsonValue(PrefenceConstants.ADRS_DATA, AddressBean::class.java)

        with(param)
        {
            deviceToken = mPreferenceHelper.getKeyValue(DataNames.REGISTRATION_ID, PrefenceConstants.TYPE_STRING).toString()
            deviceType = 1
            email = acct.email ?: ""
            googleToken = acct.uid ?: ""
            name = acct.displayName ?: ""
            image = acct.photoUrl.toString() ?: ""
            latitude = mLocUser?.latitude?.toDoubleOrNull() ?: 0.0
            longitude = mLocUser?.longitude?.toDoubleOrNull() ?: 0.0
        }

        if (mLoginViewModel != null) {
            mLoginViewModel?.validateGoogle(param)
        } else {
            loginInput?.invoke(param)
        }

    }

    fun logoutGoogle(afterLogout: (String) -> Unit) {

        if (mGoogleSignInClient != null && ::activity.isInitialized) {
            mGoogleSignInClient?.revokeAccess()?.addOnCompleteListener(activity) {
                afterLogout.invoke("success")
            }
        } else {
            afterLogout.invoke("success")
        }


        FirebaseAuth.getInstance().signOut()
    }

}