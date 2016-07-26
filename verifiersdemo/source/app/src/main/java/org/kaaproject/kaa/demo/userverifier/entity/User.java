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

package org.kaaproject.kaa.demo.userverifier.entity;

import org.kaaproject.kaa.demo.userverifier.util.UserVerifierConstants;

/**
 * Inner entity-wrapper on Kaa entity
 */
public class User {

    private String id;
    private String name;
    private String token;
    private String currentInfo;
    private String eventMessagesText;

    private UserVerifierConstants.AccountType type;

    public User(UserVerifierConstants.AccountType type, String id, String name, String token) {
        this.name = name;
        this.id = id;
        this.token = token;
        this.type = type;
    }

    public void setCurrentInfo(String currentInfo) {
        this.currentInfo = currentInfo;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEventMessagesText() {
        return eventMessagesText;
    }

    public void setEventMessagesText(String eventMessagesText) {
        this.eventMessagesText = eventMessagesText;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getToken() {
        return token;
    }

    public String getCurrentInfo() {
        return currentInfo;
    }

    public UserVerifierConstants.AccountType getType() {
        return type;
    }


}
