/**
 *  Copyright 2014-2016 CyberVision, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kaaproject.kaa.demo.iotworld.irrigation;

import org.kaaproject.kaa.client.DesktopKaaPlatformContext;
import org.kaaproject.kaa.client.Kaa;
import org.kaaproject.kaa.client.KaaClient;
import org.kaaproject.kaa.client.SimpleKaaClientStateListener;
import org.kaaproject.kaa.client.event.registration.UserAttachCallback;
import org.kaaproject.kaa.common.endpoint.gen.UserAttachResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IMSRunner {

    private static final String DEFAULT_USER_NAME = "kaa";
    private static final String DEFAULT_ACCESS_CODE = "IRRIGATION_SYSTEM_ACCESS_CODE";

    private static final Logger LOG = LoggerFactory.getLogger(IMSRunner.class);

    private static KaaClient kaaClient;

    private static volatile boolean running = true;

    public static void main(String[] args) {
        LOG.info("Raspberry PI 2 controller started");
        LOG.info("--= Press any key to exit =--");

        kaaClient = Kaa.newClient(new DesktopKaaPlatformContext(), new SimpleKaaClientStateListener() {
            @Override
            public void onStarted() {
                LOG.info("Kaa client started");
                try {
                    kaaClient.attachUser(DEFAULT_USER_NAME, "", new UserAttachCallback() {
                        @Override
                        public void onAttachResult(UserAttachResponse arg0) {
                            new IMSController(kaaClient).init();
                        }
                    });
                } catch (Exception e) {
                    LOG.error("Got error during running IMS controller", e);
                }
            }

            @Override
            public void onStopped() {
                LOG.info("Kaa client stopped");
            }
        });

        try {
            kaaClient.setEndpointAccessToken(DEFAULT_ACCESS_CODE);
            kaaClient.start();
        } catch (Exception e) {
            LOG.info("Got exception during running kaa client", e);
        }

        final Thread mainThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                running = false;
                mainThread.interrupt();
                try {
                    mainThread.join();
                } catch (InterruptedException e) {
                    LOG.error("Interrupted during await termination!", e);
                }
            }
        });

        while (running) {
            try {
                Thread.sleep(60 * 1000L);
            } catch (InterruptedException e) {
                if (running) {
                    LOG.error("Interrupted during execution!", e);
                } else {
                    LOG.info("Received shutdown request!");
                }
            }
        }

        kaaClient.stop();
    }
}
