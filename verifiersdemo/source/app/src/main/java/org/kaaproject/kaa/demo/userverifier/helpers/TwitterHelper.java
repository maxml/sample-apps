/**
 * Copyright 2014-2016 CyberVision, Inc.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kaaproject.kaa.demo.userverifier.helpers;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import io.fabric.sdk.android.Fabric;
import org.kaaproject.kaa.demo.userverifier.entity.User;
import org.kaaproject.kaa.demo.userverifier.util.UserVerifierConstants;
import org.kaaproject.kaa.demo.verifiersdemo.R;

/**
 * Extends {@see SocialNetworkHelper}.
 * Helper class for Twitter Sign In functionality.
 *
 * @see <a href="https://fabric.io/downloads/android">Twitter Sign In Manual</a>
 */
public class TwitterHelper extends SocialNetworkHelper {

    private TwitterLoginButton twitterButton;

    public TwitterHelper(Handler callback, AppCompatActivity activity) {
        super(callback, activity);
    }

    /**
     * Creating a Twitter authConfig for Twitter credentials verification.
     */
    public void init() {
        TwitterAuthConfig authConfig = new TwitterAuthConfig(
                getContext().getString(R.string.twitter_key),
                getContext().getString(R.string.twitter_secret));
        Fabric.with(getContext(), new Twitter(authConfig));
    }

    public void initSignInButton(Object button) {
        twitterButton = (TwitterLoginButton) button;

        twitterButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // The TwitterSession is also available through:
                // Twitter.getInstance().core.getSessionManager().getActiveSession()
                TwitterSession session = result.data;

                String id = "" + session.getId();
                String name = session.getUserName();
                String token = getContext().getString(R.string.twitter_secret);

                User user = new User(UserVerifierConstants.AccountType.TWITTER, id, name, token);

                getCallback().sendMessage(getCallback().obtainMessage(
                        UserVerifierConstants.HANDLER_ATTACH_USER_ACTION, user));
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Login with Twitter failure", exception);

                getCallback().sendMessage(getCallback().obtainMessage(
                        UserVerifierConstants.HANDLER_ERROR_ACTION, "Error in Twitter: " + exception.getMessage()));
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        twitterButton.onActivityResult(requestCode, resultCode, data);
    }

    public void logout() {
        TwitterSession twitterSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
        if (twitterSession != null) {
            clearCookies(getContext());

            Twitter.getSessionManager().clearActiveSession();
            Twitter.logOut();
        }
    }

    private void clearCookies(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }
}
