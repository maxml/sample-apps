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

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

/**
 * Abstract class for all social network verifier' helpers.
 *
 * @see <a href="http://docs.kaaproject.org/display/KAA/Creating+custom+user+verifier">Kaa User verifier</a>
 */
public abstract class SocialNetworkHelper {

    private Handler callback;
    private AppCompatActivity activity;

    public SocialNetworkHelper(Handler callback, AppCompatActivity activity) {
        this.callback = callback;
        this.activity = activity;
    }

    /**
     * Initial functionality, what must be loaded before user sign in
     */
    public abstract void init();

    /**
     * All social networks work with their buttons
     *
     * @param button
     */
    public abstract void initSignInButton(Object button);

    /**
     * Method, that execute some code from result intent from social network server
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public abstract void onActivityResult(int requestCode, int resultCode, Intent data);

    /**
     * Social logout functionality
     */
    public abstract void logout();

    public Handler getCallback() {
        return callback;
    }

    public void setCallback(Handler callback) {
        this.callback = callback;
    }

    public AppCompatActivity getActivity() {
        return activity;
    }

    public Context getContext() {
        return activity;
    }

    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
    }
}
