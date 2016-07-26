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
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import org.kaaproject.kaa.demo.userverifier.entity.User;
import org.kaaproject.kaa.demo.userverifier.util.UserVerifierConstants;

/**
 * Extends {@see SocialNetworkHelper}.
 * Helper class for Facebook Sign In functionality.
 *
 * @see <a href="https://developers.facebook.com/docs/facebook-login/android/">Facebook Sign In Manual</a>
 */
public class FacebookHelper extends SocialNetworkHelper {

    /**
     * Facebook UI helper class used for managing the login UI.
     */
    private CallbackManager facebookCallback;

    public FacebookHelper(Handler callback, AppCompatActivity activity) {
        super(callback, activity);
    }

    @Override
    public void init() {
    }

    public void initSignInButton(Object button) {
        LoginButton loginButton = (LoginButton) button;
        loginButton.setReadPermissions("public_profile");
        // Other app specific specialization
        facebookCallback = CallbackManager.Factory.create();

        // Callback registration
        loginButton.registerCallback(facebookCallback, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String id = "" + loginResult.getAccessToken().getUserId();
                String name = Profile.getCurrentProfile().getName();
                String token = loginResult.getAccessToken().getToken();

                User user = new User(UserVerifierConstants.AccountType.FACEBOOK, id, name, token);

                getCallback().sendMessage(getCallback().obtainMessage(
                        UserVerifierConstants.HANDLER_ATTACH_USER_ACTION, user));
            }

            @Override
            public void onCancel() {
                getCallback().sendMessage(getCallback().obtainMessage(
                        UserVerifierConstants.HANDLER_ERROR_ACTION, "Cancel in Facebook connection!"));
            }

            @Override
            public void onError(FacebookException exception) {
                getCallback().sendMessage(getCallback().obtainMessage(
                        UserVerifierConstants.HANDLER_ERROR_ACTION,
                        "Error in Facebook connection!" + exception.getMessage()));
            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        facebookCallback.onActivityResult(requestCode, resultCode, data);
    }


    public void logout() {
        LoginManager.getInstance().logOut();
    }

}

