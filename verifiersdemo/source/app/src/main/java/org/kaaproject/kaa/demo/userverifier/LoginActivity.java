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

package org.kaaproject.kaa.demo.userverifier;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import org.kaaproject.kaa.demo.userverifier.entity.User;
import org.kaaproject.kaa.demo.userverifier.helpers.FacebookHelper;
import org.kaaproject.kaa.demo.userverifier.helpers.GplusHelper;
import org.kaaproject.kaa.demo.userverifier.helpers.TwitterHelper;
import org.kaaproject.kaa.demo.userverifier.kaa.KaaManager;
import org.kaaproject.kaa.demo.userverifier.util.UserVerifierConstants;
import org.kaaproject.kaa.demo.verifiersdemo.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Class extends {@link AppCompatActivity}.
 * Sign in with three social network - Facebook, Google, Twitter.
 */
public class LoginActivity extends AppCompatActivity {

    public enum EventStatus {
        RECEIVED, SENT
    }

    private TextView greetingTextView;
    private TextView idTextView;
    private TextView infoTextView;
    private EditText messageEdit;
    private EditText eventMessagesEdit;

    private Button sendEventButton;

    private TwitterHelper twitterHelper;
    private FacebookHelper facebookHelper;
    private GplusHelper gplusHelper;

    private KaaManager manager;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initHelpers();

        setContentView(R.layout.activity_logins);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        /*
         * Sets the Toolbar to act as the ActionBar for this Activity window.
         */
        setSupportActionBar(toolbar);

        greetingTextView = (TextView) findViewById(R.id.greeting);
        idTextView = (TextView) findViewById(R.id.idText);
        infoTextView = (TextView) findViewById(R.id.infoText);

        sendEventButton = (Button) findViewById(R.id.sendEventButton);

        messageEdit = (EditText) findViewById(R.id.msgBox);
        eventMessagesEdit = (EditText) findViewById(R.id.eventMessages);

        manager = new KaaManager(activityCalback);
        manager.start(this);

        /*
         *   Register the application using Fabric plug-in for managing Twitter apps
         */
        TwitterLoginButton twitterButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        twitterHelper.initSignInButton(twitterButton);


        /*
         * Creating the Google API client capable of making requests for tokens, user info etc.
         */
        SignInButton googleButton = (SignInButton) findViewById(R.id.gplus_sign_in_button);
        gplusHelper.initSignInButton(googleButton);


        /*
         *  Creates listener for Facebook.
         */
        LoginButton facebookButton = (LoginButton) findViewById(R.id.facebook_login_button);
        facebookHelper.initSignInButton(facebookButton);

        sendEventButton.setOnClickListener(new SendEventButtonClickListener());
    }

    private void initHelpers() {
        gplusHelper = new GplusHelper(activityCalback, this);
        facebookHelper = new FacebookHelper(activityCalback, this);
        twitterHelper = new TwitterHelper(activityCalback, this);

        twitterHelper.init();
        gplusHelper.init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        twitterHelper.onActivityResult(requestCode, resultCode, data);
        facebookHelper.onActivityResult(requestCode, resultCode, data);
        gplusHelper.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onResume() {
        super.onResume();
        updateViews();

        /*
         * Notify the application of the foreground state.
         */
        manager.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        /*
         * Notify the application of the background state.
         */
        manager.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        manager.stop();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logins, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_logout:
                if (user != null)
                    logout(user.getType());

                manager.detachEndpoint();
                user = null;

                updateViews();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout(UserVerifierConstants.AccountType type) {
        switch (type) {
            case GOOGLE:
                gplusHelper.logout();
                break;
            case FACEBOOK:
                facebookHelper.logout();
                break;
            case TWITTER:
                twitterHelper.logout();
                break;
        }
    }

    private void attachUser(User user) {
        this.user = user;

        manager.attachUser(user);
    }

    private void updateViews() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (user == null) {
                    sendEventButton.setEnabled(false);
                    findViewById(R.id.login_info).setVisibility(View.INVISIBLE);
                    findViewById(R.id.login_buttons).setVisibility(View.VISIBLE);
                    return;
                }

                sendEventButton.setEnabled(true);
                findViewById(R.id.login_info).setVisibility(View.VISIBLE);
                findViewById(R.id.login_buttons).setVisibility(View.INVISIBLE);

                greetingTextView.setText(user.getName());
                idTextView.setText(user.getId());
                infoTextView.setText(user.getCurrentInfo());
                if (user.getEventMessagesText() != null) {
                    eventMessagesEdit.setText(user.getEventMessagesText());
                }
            }
        });
    }


    private void addNewEventToChatBox(final EventStatus status, final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat format = new SimpleDateFormat("[HH:mm:ss] ");

                if (message != null && message.length() > 0) {
                    eventMessagesEdit.setText(format.format(calendar.getTime()) + " " + status +
                            ": " + message + "\n" + eventMessagesEdit.getText());

                    user.setEventMessagesText(eventMessagesEdit.getText().toString());
                    messageEdit.setText(null);
                }
            }
        });
    }

    private class SendEventButtonClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            String message = messageEdit.getText().toString();
            addNewEventToChatBox(EventStatus.SENT, message);
            if (message.length() > 0) {
                Log.i(UserVerifierConstants.TAG, "Sending event: " + message);
                manager.sendEventToAll(message);
            }
        }
    }

    private Handler activityCalback = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case UserVerifierConstants.HANDLER_SEND_MESSAGE_ACTION:
                    addNewEventToChatBox(EventStatus.RECEIVED, (String) msg.obj);
                    break;
                case UserVerifierConstants.HANDLER_ERROR_ACTION:
                    Toast.makeText(LoginActivity.this, "Error: " + msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                case UserVerifierConstants.HANDLER_UPDATE_VIEW_ACTION:
                    updateViews();
                    break;
                case UserVerifierConstants.HANDLER_ATTACH_USER_ACTION:
                    attachUser((User) msg.obj);
                    break;
            }
        }
    };
}
