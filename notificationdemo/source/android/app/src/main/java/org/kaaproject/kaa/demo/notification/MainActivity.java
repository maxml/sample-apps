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

package org.kaaproject.kaa.demo.notification;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import org.kaaproject.kaa.client.notification.NotificationListener;
import org.kaaproject.kaa.client.notification.NotificationTopicListListener;
import org.kaaproject.kaa.common.endpoint.gen.Topic;
import org.kaaproject.kaa.demo.notification.entity.TopicPojo;
import org.kaaproject.kaa.demo.notification.fragment.NotificationDialogFragment;
import org.kaaproject.kaa.demo.notification.fragment.NotificationFragment;
import org.kaaproject.kaa.demo.notification.fragment.OnFragmentUpdateEvent;
import org.kaaproject.kaa.demo.notification.fragment.TopicFragment;
import org.kaaproject.kaa.demo.notification.kaa.KaaManager;
import org.kaaproject.kaa.demo.notification.storage.TopicStorage;
import org.kaaproject.kaa.demo.notification.util.NotificationConstants;
import org.kaaproject.kaa.demo.notification.util.TopicHelper;
import org.kaaproject.kaa.schema.sample.notification.SecurityAlert;

import java.util.List;

/**
 * The implementation of the {@link FragmentActivity} class.
 * Use multithreading for starting Kaa client
 * Init Notification and Topic listener, which can get information from server
 * Manage all views.
 * <p>
 * Tip: you can use Service with this AsyncTask and send all Kaa Notification in mobile notification area
 */
public class MainActivity extends FragmentActivity implements TopicFragment.OnTopicClickedListener {

    private KaaManager manager;
    private NotificationListener notificationListener;
    private NotificationTopicListListener topicListener;
    private ProgressBar progressBar;
    private FrameLayout container;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_main);

        manager = new KaaManager(MainActivity.this);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        container = (FrameLayout) findViewById(R.id.container);

        kaaTask.execute();
        permitPolicy();
    }

    @Override
    protected void onResume() {
        super.onResume();

//         Notify the application of the foreground state.
        manager.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

//        Notify the application of the background state.
        manager.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        manager.onStop();
        kaaTask.cancel(true);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentByTag(NotificationConstants.NOTIFICATION_FRAGMENT_TAG) != null) {
            showProgressBar(true);
            showTopicsFragment();
            showProgressBar(false);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onTopicClicked(int position) {
        showNotificationsFragment(position);
    }

//         Tip: you can use it from Service
    private AsyncTask<Void, Void, Void> kaaTask = new AsyncTask<Void, Void, Void>() {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressBar(true);
        }

        @Override
        protected Void doInBackground(Void... params) {
//             Initialize a notification listener and add it to the Kaa client.
            initNotificationListener();

//             Initialize a topicList listener and add it to the Kaa client.
            initTopicListener();

            manager.start(notificationListener, topicListener);

            List<TopicPojo> buff = TopicHelper.sync(TopicStorage.get().getTopics(), manager.getTopics());

            TopicStorage.get()
                    .setTopics(buff)
                    .save(MainActivity.this);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            showProgressBar(false);
            showTopicsFragment();
        }
    };

    public KaaManager getManager() {
        return manager;
    }

    private void initNotificationListener() {
        notificationListener = new NotificationListener() {
            public void onNotification(final long topicId, final SecurityAlert alert) {
                Log.i(NotificationConstants.TAG, "Notification received: " + alert.toString());

                List<TopicPojo> updatedTopics = TopicHelper.addNotification(TopicStorage.get().getTopics(),
                        topicId, alert);
                TopicStorage.get().setTopics(updatedTopics).save(MainActivity.this);

                switch (getCurrentFragmentTag()) {
                    case NotificationConstants.TOPIC_FRAGMENT_TAG:
                        showNotificationDialog(topicId, alert);
                    case NotificationConstants.NOTIFICATION_FRAGMENT_TAG:
                        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(getCurrentFragmentTag());

                        ((OnFragmentUpdateEvent) currentFragment).onRefresh();
                        break;
                    default:
                        Log.i(NotificationConstants.TAG, "Wrong fragment!" + getCurrentFragmentTag());

                }
            }
        };
    }

    private String getCurrentFragmentTag() {
        return getSupportFragmentManager().findFragmentById(R.id.container).getTag();
    }

    private void showNotificationDialog(long topicId, SecurityAlert notification) {

        NotificationDialogFragment dialog = NotificationDialogFragment.newInstance(TopicHelper.getTopicName(TopicStorage.get().getTopics(), topicId),
                notification.getAlertMessage(), null);
        dialog.show(getSupportFragmentManager(), "fragment_alert");
    }

    private void initTopicListener() {
        topicListener = new NotificationTopicListListener() {
            public void onListUpdated(List<Topic> topics) {
                Log.i(NotificationConstants.TAG, "Topic list updated with topics: ");
                for (Topic topic : topics) {
                    Log.i(NotificationConstants.TAG, topic.toString());
                }

                TopicFragment topicFragment = (TopicFragment) getSupportFragmentManager().findFragmentByTag(NotificationConstants.TOPIC_FRAGMENT_TAG);
                if (topicFragment != null && topicFragment.isVisible()) {
                    topicFragment.onRefresh();
                }
            }
        };
    }

    private void permitPolicy() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    private void showNotificationsFragment(int position) {
        NotificationFragment notificationFragment = new NotificationFragment();

        Bundle args = new Bundle();
        args.putInt(NotificationConstants.BUNDLE_TOPIC_ID, position);
        notificationFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, notificationFragment,
                NotificationConstants.NOTIFICATION_FRAGMENT_TAG).commit();
    }

    private void showTopicsFragment() {
        TopicFragment topicFragment = new TopicFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, topicFragment,
                NotificationConstants.TOPIC_FRAGMENT_TAG).commit();
    }

    private void showProgressBar(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            container.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            container.setVisibility(View.VISIBLE);
        }
    }

}