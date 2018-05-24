package infinity.to.loop.betteryoutube.main

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.view.View
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import infinity.to.loop.betteryoutube.R
import infinity.to.loop.betteryoutube.home.HomeActivity
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.ResponseTypeValues
import javax.inject.Inject


class MainViewModel @Inject constructor(private val context: Activity,
                                        private val clientID: String,
                                        private val authState: AuthState,
                                        private val authService: AuthorizationService) {

    lateinit var mGoogleSignInClient: GoogleSignInClient

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(context, gso)

    }

    fun sign(v: View) {
        val signInIntent = mGoogleSignInClient.signInIntent
        context.startActivityForResult(signInIntent, 200)
    }


    fun authenticate(v: View) {

        val authRequest = AuthorizationRequest.Builder(
                authState.authorizationServiceConfiguration!!,
                clientID,
                ResponseTypeValues.CODE,
                Uri.parse(context.getString(R.string.redirect_url)))
                .setScope(context.getString(R.string.scopes))
                .build()


        val completionIntent = Intent(context, HomeActivity::class.java)
        completionIntent.putExtra("COMPLETE", true)

        authService.performAuthorizationRequest(authRequest,
                PendingIntent.getActivity(v.context, 0, completionIntent, 0))

    }
}