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

package org.kaaproject.kaa.demo.userverifier.kaa;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import org.kaaproject.kaa.client.AndroidKaaPlatformContext;
import org.kaaproject.kaa.client.Kaa;
import org.kaaproject.kaa.client.KaaClient;
import org.kaaproject.kaa.client.SimpleKaaClientStateListener;
import org.kaaproject.kaa.client.event.EndpointKeyHash;
import org.kaaproject.kaa.client.event.EventFamilyFactory;
import org.kaaproject.kaa.client.event.FindEventListenersCallback;
import org.kaaproject.kaa.client.event.registration.OnDetachEndpointOperationCallback;
import org.kaaproject.kaa.client.event.registration.UserAttachCallback;
import org.kaaproject.kaa.common.endpoint.gen.SyncResponseResultType;
import org.kaaproject.kaa.common.endpoint.gen.UserAttachResponse;
import org.kaaproject.kaa.demo.userverifier.entity.User;
import org.kaaproject.kaa.demo.userverifier.util.UserVerifierConstants;
import org.kaaproject.kaa.demo.verifiersdemo.KaaVerifiersTokens;
import org.kaaproject.kaa.demo.verifiersdemo.MessageEvent;
import org.kaaproject.kaa.demo.verifiersdemo.VerifiersDemoEventClassFamily;

import java.util.LinkedList;
import java.util.List;

public class KaaManager {

    private KaaClient mClient;
    /**
     * Kaa verifiers tokens for Google, Facebook and Twitter
     */
    private KaaVerifiersTokens verifiersTokens;

    private VerifiersDemoEventClassFamily vdecf;
    /**
     * Defines Kaa event listener
     */
    private KaaEventListener listener;

    private Handler activityCalback;

    public KaaManager(Handler activityCalback) {
        this.activityCalback = activityCalback;
    }

    /**
     * Initialize the Kaa client using the Android context.
     * Start the Kaa client workflow.
     */
    public void start(Context context) {
        mClient = Kaa.newClient(new AndroidKaaPlatformContext(context), new SimpleKaaClientStateListener() {
            @Override
            public void onStarted() {
                super.onStarted();

                verifiersTokens = mClient.getConfiguration();
                Log.i(UserVerifierConstants.TAG, "Verifiers tokens: " + verifiersTokens.toString());


                EventFamilyFactory eventFamilyFactory = mClient.getEventFamilyFactory();
                vdecf = eventFamilyFactory.getVerifiersDemoEventClassFamily();
            }
        }, true);

        mClient.start();
    }

    public void sendEventToAll(String message) {
        vdecf.sendEventToAll(new MessageEvent(message));
    }

    public void attachUser(final User user) {
        user.setName(user.getType() + " user name: " + user.getName());
        user.setId(user.getType() + " user id: " + user.getId());
        user.setCurrentInfo("Waiting for Kaa response...");

        Log.i(UserVerifierConstants.TAG, user.getCurrentInfo());
        activityCalback.sendEmptyMessage(UserVerifierConstants.HANDLER_UPDATE_VIEW_ACTION);

        Log.i(UserVerifierConstants.TAG, "Attaching user...");
        String kaaVerifierToken = getKaaVerifierToken(user);

        mClient.attachUser(kaaVerifierToken, user.getId(), user.getToken(), new UserAttachCallback() {
            @Override
            public void onAttachResult(UserAttachResponse userAttachResponse) {
                if (userAttachResponse.getResult() != SyncResponseResultType.SUCCESS) {
                    onErrorResult(userAttachResponse);
                    return;
                }
                onSuccessResult(userAttachResponse);
            }

            private void onSuccessResult(UserAttachResponse userAttachResponse) {
                user.setCurrentInfo("Successful Kaa verification");
                Log.i(UserVerifierConstants.TAG, "User was attached... " + userAttachResponse.toString());

                activityCalback.sendEmptyMessage(UserVerifierConstants.HANDLER_UPDATE_VIEW_ACTION);

                findEventListeners();
                updateListeners();
            }

            /*
             *  Remove old listener to avoid duplication of messages.
             */
            private void updateListeners() {
                if (listener != null) {
                    vdecf.removeListener(listener);
                }
                listener = new KaaEventListener();
                vdecf.addListener(listener);
            }

            private void findEventListeners() {
                List<String> FQNs = new LinkedList<>();
                FQNs.add("org.kaaproject.kaa.demo.verifiersdemo.MessageEvent");

                mClient.findEventListeners(FQNs, new FindEventListenersCallback() {
                    @Override
                    public void onRequestFailed() {
                        Log.i(UserVerifierConstants.TAG, "Find event listeners request has failed");
                    }

                    @Override
                    public void onEventListenersReceived(List<String> eventListeners) {
                        Log.i(UserVerifierConstants.TAG, "Event listeners received: " + eventListeners);
                    }
                });
            }

            private void onErrorResult(UserAttachResponse userAttachResponse) {
                String failureString = userAttachResponse.getErrorReason() == null ?
                        userAttachResponse.getErrorCode().toString() :
                        userAttachResponse.getErrorReason();

                user.setCurrentInfo("Kaa verification failure: " + failureString);
                Log.i(UserVerifierConstants.TAG, user.getCurrentInfo());

                activityCalback.sendEmptyMessage(UserVerifierConstants.HANDLER_UPDATE_VIEW_ACTION);
                activityCalback.sendMessage(activityCalback.obtainMessage(UserVerifierConstants.HANDLER_ERROR_ACTION, failureString));
            }
        });
    }

    private String getKaaVerifierToken(User user) {
        String kaaVerifierToken = null;
        switch (user.getType()) {
            case GOOGLE:
                kaaVerifierToken = verifiersTokens.getGoogleKaaVerifierToken();
                break;
            case FACEBOOK:
                kaaVerifierToken = verifiersTokens.getFacebookKaaVerifierToken();
                break;
            case TWITTER:
                kaaVerifierToken = verifiersTokens.getTwitterKaaVerifierToken();
                break;
        }
        return kaaVerifierToken;
    }

    /**
     * Detach the endpoint from the user.
     */
    public void detachEndpoint() {
        EndpointKeyHash endpointKey = new EndpointKeyHash(mClient.getEndpointKeyHash());
        mClient.detachEndpoint(endpointKey, new OnDetachEndpointOperationCallback() {
            @Override
            public void onDetach(SyncResponseResultType syncResponseResultType) {
                Log.i(UserVerifierConstants.TAG, "User was detached");
            }
        });
    }

    /**
     * Suspend the Kaa client. Release all network connections and application
     * resources. Suspend all the Kaa client tasks.
     */
    public void pause() {
        mClient.pause();
    }

    /**
     * Resume the Kaa client. Restore the Kaa client workflow. Resume all the Kaa client
     * tasks.
     */
    public void resume() {
        mClient.resume();
    }

    /**
     * Stop the Kaa client. Release all network connections and application
     * resources. Shut down all the Kaa client tasks.
     */
    public void stop() {
        mClient.stop();
    }


    /**
     * Class is used to handle events
     */
    private class KaaEventListener implements VerifiersDemoEventClassFamily.Listener {
        @Override
        public void onEvent(MessageEvent messageEvent, String sourceEndpoint) {
            Log.i(UserVerifierConstants.TAG, "Event was received: " + messageEvent.getMessage());

            Message message = activityCalback.obtainMessage(UserVerifierConstants.HANDLER_SEND_MESSAGE_ACTION,
                    messageEvent.getMessage());
            activityCalback.sendMessage(message);
        }
    }

}
