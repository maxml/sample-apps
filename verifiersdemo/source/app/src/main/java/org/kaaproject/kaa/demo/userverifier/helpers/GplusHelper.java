/**
 * Copyright 2014-2016 CyberVision, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kaaproject.kaa.demo.userverifier.helpers;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import org.kaaproject.kaa.demo.userverifier.entity.User;
import org.kaaproject.kaa.demo.userverifier.util.UserVerifierConstants;
import org.kaaproject.kaa.demo.verifiersdemo.R;

/**
 * Extends {@see SocialNetworkHelper}.
 * Helper class for Google Sign In functionality.
 *
 * @see <a href="https://developers.google.com/identity/sign-in/android/sign-in">Google Sign In Manual</a>
 */
public class GplusHelper extends SocialNetworkHelper implements GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions gso;

    public GplusHelper(Handler callback, AppCompatActivity activity) {
        super(callback, activity);
    }

    /**
     * Configure sign-in to request the user's ID, email address, and basic
     * profile. ID and basic profile are included in DEFAULT_SIGN_IN.
     * Build a GoogleApiClient with access to the Google Sign-In API and the
     * options specified by gso.
     */
    public void init() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestServerAuthCode(getContext().getString(R.string.google_server_client_id))
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity() /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    public void initSignInButton(Object button) {
        SignInButton signInButton = (SignInButton) button;
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                getActivity().startActivityForResult(signInIntent, UserVerifierConstants.RC_SIGN_IN);
            }
        });
    }

    /**
     * Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == UserVerifierConstants.RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
        }
    }

    private void handleGoogleSignInResult(GoogleSignInResult result) {
        Log.d(UserVerifierConstants.TAG, "handleGoogleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            String id = acct.getId();
            String name = acct.getDisplayName();
            String token = acct.getServerAuthCode();
            User user = new User(UserVerifierConstants.AccountType.GOOGLE, id, name, token);

            getCallback().sendMessage(getCallback().obtainMessage(
                    UserVerifierConstants.HANDLER_ATTACH_USER_ACTION, user));
        } else {
            getCallback().sendMessage(getCallback().obtainMessage(
                    UserVerifierConstants.HANDLER_ERROR_ACTION, "Error in Google authentication!"));
        }
    }

    public void logout() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                Toast.makeText(getContext(), "Success logout!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        getCallback().sendMessage(getCallback().obtainMessage(
                UserVerifierConstants.HANDLER_ERROR_ACTION, "Error in Google connection!"));
    }
}
